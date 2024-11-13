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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.grating.recolldroid.R
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.theme.RecollDroidTheme

@Composable
fun QueryScreen(
    viewModel: RecollDroidViewModel = viewModel(),
    onQueryChanged: (query: String) -> Unit,
    onQueryExecuteRequest: () -> Unit
) {
    QueryBar(viewModel, onQueryChanged, onQueryExecuteRequest)
}

@Composable
fun QueryBar(
    viewModel: RecollDroidViewModel = viewModel(),
    onQueryChanged: (query: String) -> Unit,
    onQueryExecuteRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = uiState.currentQuery,
        onValueChange = {
            onQueryChanged(it)
        },
        placeholder = { Text(stringResource(R.string.enter_recoll_query)) },
        leadingIcon = {
            Icon(painter = painterResource(id = R.drawable.recoll),
                 contentDescription = stringResource(R.string.recoll_icon),
                 tint = Color.Unspecified,
                 modifier = Modifier.size(32.dp))
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp).fillMaxWidth(),
        enabled = true,
        singleLine = true,
        trailingIcon = {
            OutlinedIconButton(
                onClick = { onQueryExecuteRequest() }
            ) {
                Icon(imageVector = Icons.Outlined.Search,
                     contentDescription = stringResource(R.string.search_action))
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
            onQueryExecuteRequest()
        }),
    )
}

@Preview(showBackground = true)
@Composable
fun QueryScreenPreview() {
    RecollDroidTheme {
        QueryScreen(onQueryChanged = {},
                    onQueryExecuteRequest = {})
    }
}