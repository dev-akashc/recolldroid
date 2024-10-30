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
package org.grating.recolldroid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.ui.LoadingError
import org.grating.recolldroid.ui.LoadingSpinner
import org.grating.recolldroid.ui.doHighlight
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.model.SnippetsResponse
import org.grating.recolldroid.ui.simpleVerticalScrollbar

@Composable
fun SnippetsScreen(
    viewModel: RecollDroidViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState().value
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .simpleVerticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        when (val response = uiState.currentSnippets) {
            is SnippetsResponse.Success -> {
                Row {
                    Text(text = "Page",
                         modifier = Modifier
                             .padding(4.dp)
                             .weight(1f))
                    Text(text = "Snippet",
                         modifier = Modifier
                             .padding(4.dp)
                             .weight(5f))
                }
                response.result.forEach { snippet ->
                    Row {
                        Text(text = snippet.page.toString(),
                             modifier = Modifier
                                 .padding(4.dp)
                                 .weight(1f))
                        Text(text = snippet.snippet.doHighlight(),
                             modifier = Modifier
                                 .padding(4.dp)
                                 .weight(5f))
                    }
                }
            }

            is SnippetsResponse.Error -> LoadingError(response.errorMsg)
            is SnippetsResponse.Pending -> LoadingSpinner()
            SnippetsResponse.None ->
                Text(text = "Yoo wot m8?")
        }
    }
}