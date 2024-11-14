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
package org.grating.recolldroid.ui.model

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Base64
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.grating.recolldroid.RecollDroidApplication
import org.grating.recolldroid.data.DownloadedDocumentLatch
import org.grating.recolldroid.data.ErrorLatch
import org.grating.recolldroid.data.RecollSearchResult
import org.grating.recolldroid.data.ResultsRepository
import org.grating.recolldroid.network.BasicAuthOverHttpException
import org.grating.recolldroid.ui.DEFAULT_SEARCH_HIST_SZ
import org.grating.recolldroid.ui.data.DownloadAccount
import org.grating.recolldroid.ui.data.RecollDroidSettings
import org.grating.recolldroid.ui.data.SettingsRepository
import org.grating.recolldroid.ui.logError
import org.grating.recolldroid.ui.logInfo

class RecollDroidViewModel(
    val errorLatch: ErrorLatch,
    val ddLatch: DownloadedDocumentLatch,
    private val resultsRepository: ResultsRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecollDroidUiState())
    val uiState: StateFlow<RecollDroidUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.settingsFlow.collect { appSettings ->
                _uiState.update { state ->
                    state.copy(liveSettings = appSettings,
                               pendingSettings = appSettings.toBuilder().build())
                }
            }
        }
    }

    fun updatePendingSettings(settings: RecollDroidSettings): RecollDroidViewModel {
        _uiState.update { state ->
            state.copy(pendingSettings = settings)
        }
        return this
    }

    fun commitPendingSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateSettings(_uiState.value.pendingSettings)
        }
    }

    fun revertPendingSettings() {
        _uiState.update { state ->
            state.copy(pendingSettings = state.liveSettings.toBuilder().build())
        }
    }


    fun updateCurrentQuery(query: TextFieldValue) {
        _uiState.update { currentState ->
            currentState.copy(
                currentQuery = query
            )
        }
    }

    fun updateCurrentQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                currentQuery = TextFieldValue(
                    text = query,
                    selection = TextRange(query.length) // Move cursor to end.
                )
            )
        }
    }

    fun executeCurrentQuery() {
        _uiState.update { state -> state.copy(queryResponse = QueryResponse.Pending()) }
        _uiState.update { state ->
            state.copy(
                queryResponse = QueryResponse.Success(
                    result = resultsRepository.executeQuery(_uiState.value.currentQuery.text)
                        .cachedIn(viewModelScope)))
        }
        rememberSearch()
    }


    fun setCurrentResult(result: RecollSearchResult) {
        _uiState.update { currentState ->
            currentState.copy(
                currentResult = result
            )
        }
    }

    fun retrievePreview(result: RecollSearchResult) {
        _uiState.update { state ->
            state.copy(currentResult = result,
                       currentPreview = PreviewResponse.Pending())
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { state ->
                    state.copy(
                        currentPreview = PreviewResponse.Success(
                            resultsRepository.retrievePreview(result))
                    )
                }
            } catch (e: Throwable) {
                logError("Failed while attempting to retrieve preview.", e)
                _uiState.update { state ->
                    state.copy(
                        currentPreview = PreviewResponse.Error(e.message ?: e.toString())
                    )
                }
            }
        }
    }

    fun retrieveSnippets(result: RecollSearchResult) {
        _uiState.update { state ->
            state.copy(currentResult = result,
                       currentSnippets = SnippetsResponse.Pending())
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { state ->
                    state.copy(
                        currentSnippets = SnippetsResponse.Success(
                            resultsRepository.retrieveSnippets(result))
                    )
                }
            } catch (e: Throwable) {
                logError("Failed while attempting to retrieve snippets.", e)
                _uiState.update { state ->
                    state.copy(
                        currentSnippets = SnippetsResponse.Error(e.message ?: e.toString())
                    )
                }
            }
        }
    }

    fun setConfirmableAction(msg: String, action: () -> Unit) {
        _uiState.update { state ->
            state.copy(
                confirmationMessage = msg,
                confirmableAction = action
            )
        }
    }

    fun clearConfirmableAction() {
        _uiState.update { state ->
            state.copy(
                confirmationMessage = "",
                confirmableAction = {}
            )
        }
    }

    fun requestInternalDocumentDownload(result: RecollSearchResult, ctx: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val docExtract = resultsRepository.retrieveExtract(result)

                if (docExtract.url.isBlank())
                    errorLatch.raiseError("Couldn't extract internal document",
                                          RuntimeException(docExtract.msg))
                else {
                    // Now try to download.
                    val lSet = uiState.value.liveSettings
                    val acc =
                        lSet.downloadAccountList.find { docExtract.url.startsWith(it.baseUrl) }
                    logInfo("Using account: ${acc?.toStringSafe()}")

                    downloadResult(fileName = result.filename,
                                   url = docExtract.url,
                                   user = acc?.username ?: "",
                                   pass = acc?.password ?: "",
                                   mimeType = result.mType.rawType,
                                   context = ctx,
                                   errorLatch = errorLatch)
                }
            } catch (t: Throwable) {
                errorLatch.raiseError("Failed requesting internal document.", t)
            }
        }
    }

    fun requestDownload(result: RecollSearchResult, ctx: Context) {
        val lSet = uiState.value.liveSettings
        val url = lSet.rewriteList.fold(result.url.toString()) { url, rule ->
            rule.search.toRegex().replace(url, rule.replace)
        }
        logInfo("Doc URL rewritten to: $url")
        val acc = lSet.downloadAccountList.find { url.startsWith(it.baseUrl) }
        logInfo("Using account: ${acc?.toStringSafe()}")

        downloadResult(fileName = result.filename,
                       url = url,
                       user = acc?.username ?: "",
                       pass = acc?.password ?: "",
                       mimeType = result.mType.rawType,
                       context = ctx,
                       errorLatch = errorLatch)
    }

    private fun rememberSearch() {
        val psb = _uiState.value.pendingSettings.toBuilder()
        val pastSearches = psb.pastSearchList.toMutableSet(
            psb.searchHistorySize.orDefault(DEFAULT_SEARCH_HIST_SZ) - 1)
        pastSearches.add(_uiState.value.currentQuery.text)
        psb.clearPastSearch().addAllPastSearch(pastSearches)
        updatePendingSettings(psb.build())
        commitPendingSettings()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as RecollDroidApplication
                RecollDroidViewModel(application.container.errorLatch,
                                     application.container.downloadedDocumentLatch,
                                     resultsRepository = application.container.resultsRepository,
                                     settingsRepository = application.container.settingsRepository)
            }
        }

        fun downloadResult(
            fileName: String,
            url: String,
            user: String,
            pass: String,
            mimeType: String,
            context: Context,
            errorLatch: ErrorLatch
        ): Long {

            if (url.startsWith("http:"))
                throw BasicAuthOverHttpException(url)

            return try {
                val downloadManager = context.getSystemService(DownloadManager::class.java)
                val auth = String(Base64.encode("$user:$pass".encodeToByteArray(), Base64.DEFAULT))
                val request = DownloadManager.Request(Uri.parse(url))
                    .setMimeType(mimeType)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setTitle(fileName)
                    .addRequestHeader("Authorization", "Basic $auth")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                downloadManager.enqueue(request)
            } catch (t: Throwable) {
                errorLatch.raiseError("Failed to request download", t)
                -1L
            }
        }
    }
}

fun DownloadAccount.toStringSafe(): String {
    return """
        ${DownloadAccount::class.qualifiedName}@${System.identityHashCode(this)}
        base_url: "$baseUrl"
        password: "<not shown>"
        username: "$username"
    """.trimIndent()
}


fun Int.orDefault(def: Int): Int {
    return if (this <= 0) def else this
}

fun <E> List<E>.toMutableSet(maxSz: Int): MutableSet<E> {
    return (if (maxSz < size)
        subList(0, maxSz)
    else
        this).toMutableSet()
}