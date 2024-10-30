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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.data.ResultPreview
import org.grating.recolldroid.ui.model.CurrentResultNotSet
import org.grating.recolldroid.ui.model.RecollDroidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RawDetailsScreen(
    viewModel: RecollDroidViewModel
) {

    val uiState = viewModel.uiState.collectAsState().value
    val result = uiState.currentResult ?: throw CurrentResultNotSet()
    val data = listOf(Pair("URL", result.url),
                      Pair("Document Signiature", result.sig),
                      Pair("Multi Breaks", result.recollMultiBreaks),
                      Pair("Internal Path", result.iPath),
                      Pair("Universal Document Identifier", result.recollUdi),
                      Pair("Title", result.title),
                      Pair("Document Size", result.fBytes),
                      Pair("Author", result.author),
                      Pair("Caption", result.caption),
                      Pair("Document Text Size", result.dBytes),
                      Pair("Filename", result.filename),
                      Pair("Relevancy", result.relevancyRatingPercent),
                      Pair("File Modification Time", result.fmTime),
                      Pair("Mime Type", result.mType.rawType),
                      Pair("Original Charset", result.origCharset),
                      Pair("mTime", result.mTime),
                      Pair("File Size", result.pcBytes),
                      Pair("Key Words", result.keyWords),
                      Pair("recollAptg", result.recollAptg),
                      Pair("Data Reference Date", result.dmTime),
                      Pair("Snippets Abstract", result.snippetsAbstract),
                      Pair("Snippets List", result.snippetsList),
                      Pair("_preview", result.preview.status()),
                      Pair("_idx", result.idx)
    ).sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.first }))

    SelectionContainer {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            item {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(text = "Field",
                         modifier = Modifier
                             .padding(4.dp)
                             .weight(1f))
                    Text("Value",
                         modifier = Modifier
                             .padding(4.dp)
                             .weight(3f))
                }
            }

            itemsIndexed(items = data) { idx, item ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (idx % 2 == 0) {
                            MaterialTheme.colorScheme.surfaceDim
                        } else {
                            MaterialTheme.colorScheme.surfaceBright
                        })
                ) {
                    Text(text = item.first,
                         modifier = Modifier
                             .padding(4.dp)
                             .weight(1f))
                    Text(text = item.second.toString(),
                         modifier = Modifier
                             .padding(4.dp)
                             .weight(3f))
                }
            }
        }
    }
}

fun ResultPreview.status(): String {
    return if (this == ResultPreview.NOT_LOADED)
        "Not loaded"
    else
        "Loaded"
}
