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
package org.grating.recolldroid.ui.screens.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.data.DocType
import org.grating.recolldroid.ui.model.RecollDroidViewModel

/**
 * Dialog for choosing to filter or focus on (i.e. filter all but this) some dimension of a set of
 * search results.
 */
@Composable
fun IncludeExcludeFilterSearchDialog(
    viewModel: RecollDroidViewModel,
    message: AnnotatedString,
    docType: DocType,
    onIncludeRequest: () -> Unit,
    includeMessage: String,
    onExcludeRequest: () -> Unit,
    excludeMessage: String,
    onDismissRequest: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(docType.typeIcon),
                contentDescription = "Filter by MimeType",
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.size(20.dp))
            Text(text = message)
            Spacer(modifier = Modifier.size(20.dp))
            Row {
                TextButton(
                    onClick = {
                        onIncludeRequest()
                        viewModel.clearConfirmableAction()
                    },
                    modifier = Modifier
                ) {
                    Text(includeMessage)
                }
                TextButton(
                    onClick = {
                        onExcludeRequest()
                        viewModel.clearConfirmableAction()
                    },
                    modifier = Modifier
                ) {
                    Text(excludeMessage)
                }
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}