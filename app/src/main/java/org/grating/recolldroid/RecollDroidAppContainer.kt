/* Copyright (C) 2024 Graham Bygrave
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the
 *   Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.grating.recolldroid

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.grating.recolldroid.data.DefaultResultsRepository
import org.grating.recolldroid.data.RecollApiClient
import org.grating.recolldroid.data.ResultsRepository
import org.grating.recolldroid.network.BasicAuthOverHttpException
import org.grating.recolldroid.network.RetrofitApiClient
import org.grating.recolldroid.ui.data.DsSettingsRepository
import org.grating.recolldroid.ui.data.RecollDroidSettings
import org.grating.recolldroid.ui.data.SettingsRepository
import org.grating.recolldroid.ui.data.SettingsSerializer
import org.grating.recolldroid.ui.logError
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

// Error Latch
sealed interface AppErrorState {
    data class Error(val msg: String, val t: Throwable) : AppErrorState
    data object Ok : AppErrorState
}

class ErrorLatch(private val _errorFlow: MutableStateFlow<AppErrorState>) {
    val errorFlow: StateFlow<AppErrorState> = _errorFlow.asStateFlow()
    fun raiseError(msg: String, t: Throwable) {
        _errorFlow.value = AppErrorState.Error(msg, t)
    }
    fun clearError() {
        _errorFlow.value = AppErrorState.Ok
    }
}

val AlwaysOkErrorLatch = ErrorLatch(MutableStateFlow(AppErrorState.Ok)) // For preview screens.

// Downloaded document/file Latch
data class DownloadedDocumentDetail(val uri: Uri, val mimeType: String)
sealed interface DownloadedDocumentState {
    class Ready(val detail: DownloadedDocumentDetail) : DownloadedDocumentState
    data object Idle : DownloadedDocumentState
}

class DownloadedDocumentLatch(private val _downloadedDocumentFlow: MutableStateFlow<DownloadedDocumentState>) {
    val downloadedDocumentFlow: StateFlow<DownloadedDocumentState> =
        _downloadedDocumentFlow.asStateFlow()

    fun clearDocument() {
        _downloadedDocumentFlow.value = DownloadedDocumentState.Idle
    }
}

val AlwaysIdleDownloadedDocumentLatch =
    DownloadedDocumentLatch(MutableStateFlow(DownloadedDocumentState.Idle))

interface AppContainer {
    val errorLatch: ErrorLatch
    fun signalError(msg: String = "", t: Throwable)
    val downloadedDocumentLatch: DownloadedDocumentLatch
    fun signalDocumentArrived(ddd: DownloadedDocumentDetail)
    val resultsRepository: ResultsRepository
    val settingsRepository: SettingsRepository
}

data class RecollRepositoryConnectionSettings(
    val baseUrl: String = "",
    val username: String = "",
    val password: String = ""
)

/**
 * Initialises fully functioning services for use in a live environment.
 */
class RecollDroidAppContainer(private val appCtx: Context) : AppContainer {
    private val _errorFlow: MutableStateFlow<AppErrorState> = MutableStateFlow(AppErrorState.Ok)
    override val errorLatch: ErrorLatch = ErrorLatch(_errorFlow)
    override fun signalError(msg: String, t: Throwable) {
        when (val err = _errorFlow.value) {
            is AppErrorState.Error -> throw IllegalStateException(
                "Already in error '$err' but you said '${Error(msg, t)}")

            AppErrorState.Ok -> _errorFlow.value = AppErrorState.Error(msg, t)
        }
    }

    private val _downloadedDocumentFlow: MutableStateFlow<DownloadedDocumentState> =
        MutableStateFlow(DownloadedDocumentState.Idle)
    override val downloadedDocumentLatch: DownloadedDocumentLatch =
        DownloadedDocumentLatch(_downloadedDocumentFlow)

    override fun signalDocumentArrived(ddd: DownloadedDocumentDetail) {
        when (val currDdd = _downloadedDocumentFlow.value) {
            is DownloadedDocumentState.Ready ->
                throw IllegalStateException(
                    "Already have a downloaded document '$currDdd' that hasn't been scheduled for " +
                            "opening, but you said '$ddd'")

            DownloadedDocumentState.Idle ->
                _downloadedDocumentFlow.value = DownloadedDocumentState.Ready(ddd)
        }
    }

    private val Context.settingsStore: DataStore<RecollDroidSettings> by dataStore(
        fileName = "settings.pb",
        serializer = SettingsSerializer
    )

    override val settingsRepository: SettingsRepository by lazy {
        DsSettingsRepository(appCtx.settingsStore)
    }

    override val resultsRepository: ResultsRepository =
        DefaultResultsRepository(RecollApiClient.STUB)

    private var recollRepositoryConnectionSettings = RecollRepositoryConnectionSettings()

    init {
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            settingsRepository.settingsFlow.collect {
                reinitResultsRepository(
                    RecollRepositoryConnectionSettings(
                        it.recollAccount.baseUrl,
                        it.recollAccount.username,
                        it.recollAccount.password))
            }
        }
    }

    /**
     * Return existing repository if relevant settings are unchanged, otherwise create a new recoll
     * repository based on the new settings.
     * @param settings recoll repository connection settings.
     * @return the existing repo (if nothing's changed) otherwise a new repo base on the new connection settings.
     */
    private fun reinitResultsRepository(settings: RecollRepositoryConnectionSettings) {
        if (recollRepositoryConnectionSettings != settings) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor(Interceptor { chain ->
                        val request = chain.request().newBuilder().header(
                            name = "Authorization",
                            value = Credentials.basic(settings.username, settings.password)
                        ).build()
                        chain.proceed(request)
                    })
                    .build()
                val retrofit = Retrofit.Builder()
                    .client(client)
                    .addConverterFactory(
                        Json.asConverterFactory("application/json".toMediaType())
                    )
                    .baseUrl(settings.baseUrl)
                    .build()

                // Prevent sending plaintext passwords over an unencrypted link.
                if (settings.baseUrl.startsWith("http:"))
                    throw BasicAuthOverHttpException(settings.baseUrl)

                val retrofitApiClient: RetrofitApiClient by lazy {
                    retrofit.create(RetrofitApiClient::class.java)
                }

                (resultsRepository as DefaultResultsRepository).resetRecollApiClient(
                    retrofitApiClient)
                recollRepositoryConnectionSettings = settings
            } catch (ex: Throwable) {
                _errorFlow.value =
                    AppErrorState.Error("Failed to reinit results repository following settings update.",
                                        ex)
                logError(ex.message ?: ex.toString(), ex)
            }
        }
    }
}
