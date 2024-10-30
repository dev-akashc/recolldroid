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
package org.grating.recolldroid.data

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.grating.recolldroid.ui.data.SettingsRepository

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

