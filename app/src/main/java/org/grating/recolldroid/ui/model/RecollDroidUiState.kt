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

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.grating.recolldroid.R
import org.grating.recolldroid.data.RecollSearchResult
import org.grating.recolldroid.data.RecollSnippet
import org.grating.recolldroid.data.ResultPreview
import org.grating.recolldroid.ui.data.RecollDroidSettings


sealed interface QueryResponse {
    class Success(val result: Flow<PagingData<RecollSearchResult>>) : QueryResponse
    class Error(val errorMsg: Int) : QueryResponse
    class Pending(val pendingMsg: Int = R.string.default_loading_message) : QueryResponse
    data object None : QueryResponse
}

sealed interface PreviewResponse {
    class Success(val result: ResultPreview) : PreviewResponse
    class Error(val errorMsg: String) : PreviewResponse
    class Pending(val pendingMsg: Int = R.string.default_loading_message) : PreviewResponse
    data object None : PreviewResponse
}

sealed interface SnippetsResponse {
    class Success(val result: List<RecollSnippet>) : SnippetsResponse
    class Error(val errorMsg: String) : SnippetsResponse
    class Pending(val pendingMsg: Int = R.string.default_loading_message) : SnippetsResponse
    data object None : SnippetsResponse
}

data class RecollDroidUiState(
    val manualRefresh: Int = 0,
    val currentQuery: String = "",
    val queryResponse: QueryResponse = QueryResponse.None,
    val currentResult: RecollSearchResult? = null,
    val currentPreview: PreviewResponse = PreviewResponse.None,
    val currentSnippets: SnippetsResponse = SnippetsResponse.None,
    val currentScreenIsScrollable: Boolean = false,

    val liveSettings: RecollDroidSettings = RecollDroidSettings.getDefaultInstance(),
    val pendingSettings: RecollDroidSettings = RecollDroidSettings.getDefaultInstance(),

    val confirmationMessage: String = "",
    val confirmableAction: () -> Unit = {}
)

class CurrentResultNotSet : Exception("A current search result has not been set.")