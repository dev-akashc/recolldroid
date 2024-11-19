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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
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
import org.grating.recolldroid.ui.simpleVerticalScrollbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheatSheetScreen(
    viewModel: RecollDroidViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value
    val data = listOf(
        Pair("And", "one two   one AND two  one && two"),
        Pair("Or", "on OR two   on || two"),
        Pair("Complex boolean. OR has priority, use parentheses where needed",
             "(one AND two) OR three)"),
        Pair("Not", "-term"),
        Pair("Phrase", "\"pride and prejudice\""),
        Pair("Ordered proximity (slack = 1)", "\"pride prejudice\"o1"),
        Pair("Unordered proximity (slack = 1)", "\"pride prejudice\"po1"),
        Pair("Unordered prox. (default slack-10)", "\"prejudice pride\"p"),
        Pair("No stem expansion: capitalise", "Floor"),
        Pair("Field-specific", "author:austen title:prejudice"),
        Pair("AND inside field (no order)", "author:jane,austen"),
        Pair("OR inside field", "author:austen/bronte"),
        Pair("Field names", "title/subject/caption author/from recipient/to filename ext"),
        Pair("Directory path filter", "dir:/home/me dir:doc"),
        Pair("MIME type filter", "mime:text/plain mime:video/*"),
        Pair("Date intervals", "date:2018-01-01/2018-12-31 date:2018 date:2018-01-01/P12M"),
        Pair("Size", "size>100k size<1M"),
    )

    SelectionContainer {
        val scrollState = rememberScrollState()

        Column(modifier = Modifier
            .simpleVerticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .padding(8.dp)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(text = "What",
                     modifier = Modifier
                         .padding(4.dp)
                         .weight(1f))
                Text("Examples",
                     modifier = Modifier
                         .padding(4.dp)
                         .weight(1f))
            }
            data.forEachIndexed { idx, item ->
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
                             .weight(1f))
                }
            }
        }
    }
}
