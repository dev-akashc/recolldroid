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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.ui.LoadingError
import org.grating.recolldroid.ui.LoadingSpinner
import org.grating.recolldroid.ui.cleanup
import org.grating.recolldroid.ui.doHighlight
import org.grating.recolldroid.ui.model.CurrentResultNotSet
import org.grating.recolldroid.ui.model.PreviewResponse
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.or
import org.grating.recolldroid.ui.simpleVerticalScrollbar

@Composable
fun PreviewScreen(
    viewModel: RecollDroidViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value
    val searchResult = uiState.currentResult ?: throw CurrentResultNotSet()
    val scrollState = rememberScrollState()
    Column {
        Text(text = searchResult.title or searchResult.filename,
             style = MaterialTheme.typography.titleLarge,
             textAlign = TextAlign.Center,
             modifier = Modifier.padding(8.dp).fillMaxWidth())
        Column(
            modifier = Modifier
                .simpleVerticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (val preview = uiState.currentPreview) {
                is PreviewResponse.Success ->
                    Text(text = preview.result.preview.cleanup().doHighlight())

                is PreviewResponse.Error ->
                    LoadingError(preview.errorMsg)

                is PreviewResponse.Pending ->
                    LoadingSpinner()

                PreviewResponse.None ->
                    Text(text = "Yoo wot m8?")
            }
        }
    }
}
