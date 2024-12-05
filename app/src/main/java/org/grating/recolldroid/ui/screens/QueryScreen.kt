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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.grating.recolldroid.R
import org.grating.recolldroid.ui.SEARCH_HIST_DROPDOWN_SZ
import org.grating.recolldroid.ui.data.removePastSearchAt
import org.grating.recolldroid.ui.firstN
import org.grating.recolldroid.ui.getQueryFragment
import org.grating.recolldroid.ui.model.QueryFragment
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.theme.RecollDroidTheme

@Composable
fun QueryScreen(
    viewModel: RecollDroidViewModel = viewModel(),
    onQueryChanged: (queryField: TextFieldValue) -> Unit,
    onQueryExecuteRequest: () -> Unit,
    onGotoSearchHistory: () -> Unit,
    onQuerySupportRequested: (QueryFragment) -> Boolean,
) {
    QueryBar(viewModel,
             onQueryChanged,
             onQueryExecuteRequest,
             onGotoSearchHistory,
             onQuerySupportRequested)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryBar(
    viewModel: RecollDroidViewModel = viewModel(),
    onQueryChanged: (query: TextFieldValue) -> Unit,
    onQueryExecuteRequest: () -> Unit,
    onGotoSearchHistory: () -> Unit,
    onQuerySupportRequested: (QueryFragment) -> Boolean
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var dropdownExpanded by remember { mutableStateOf(false) }
    val iSrc = remember { MutableInteractionSource() }
        .also {
            LaunchedEffect(it) {
                it.interactions.collect { event ->
                    if (event is PressInteraction.Release) {
                        if (onQuerySupportRequested(uiState.currentQuery.getQueryFragment()))
                            dropdownExpanded = false
                    }
                }
            }
        }
    ExposedDropdownMenuBox(expanded = dropdownExpanded,
                           onExpandedChange = {
                               dropdownExpanded = it
                           }) {
        OutlinedTextField(
            value = uiState.currentQuery,
            onValueChange = {
                onQueryChanged(it)
            },
            interactionSource = iSrc,
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
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
//                .onFocusChanged {
//                    if (it.isFocused) {
//                        // If a part of the query is touched that's supported by a dialog, open
//                        // that dialog with the contents of the query portion.
//                        // uiState.currentQuery.text.uiState.currentQuery.selection.start
//                        // onQuerySupportRequested(uiState.currentQuery.getWord())
//                        dropdownExpanded = true
//                    }
//                }
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
            enabled = true,
            singleLine = false,
            trailingIcon = {
                OutlinedIconButton(
                    onClick = {
                        onQueryExecuteRequest()
                        dropdownExpanded = false
                        keyboardController?.hide()
                    },
                    modifier = Modifier.padding(4.dp),
                    enabled = uiState.currentQuery.text.isNotBlank()
                ) {
                    Icon(imageVector = Icons.Outlined.Search,
                         contentDescription = stringResource(R.string.search_action))
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onQueryExecuteRequest()
                dropdownExpanded = false
                keyboardController?.hide()
            }),
        )
        ExposedDropdownMenu(expanded = dropdownExpanded,
                            onDismissRequest = {
                                dropdownExpanded = false
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                                .focusProperties { canFocus = false }
        ) {
            uiState.liveSettings.pastSearchList.firstN(SEARCH_HIST_DROPDOWN_SZ)
                .forEachIndexed { idx, qStr ->
                    DropdownMenuItem(
                        text = { Text(text = qStr) },
                        onClick = {
                            onQueryChanged(TextFieldValue(
                                text = qStr,
                                selection = TextRange(qStr.length)))
                            dropdownExpanded = false
                        },
                        trailingIcon = {
                            OutlinedIconButton(
                                onClick = {
                                    viewModel.updatePendingSettings(
                                        uiState.pendingSettings.toBuilder()
                                            .removePastSearchAt(idx).build()
                                    ).commitPendingSettings()
                                }
                            ) {
                                Icon(imageVector = Icons.Outlined.Delete,
                                     contentDescription = stringResource(R.string.delete_action))
                            }
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                }
            DropdownMenuItem(
                text = { Text(text = "Show Full History") },
                onClick = {
                    dropdownExpanded = false
                    onGotoSearchHistory()
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QueryScreenPreview() {
    RecollDroidTheme {
        QueryScreen(onQueryChanged = {},
                    onQueryExecuteRequest = {},
                    onGotoSearchHistory = {},
                    onQuerySupportRequested = { false })
    }
}