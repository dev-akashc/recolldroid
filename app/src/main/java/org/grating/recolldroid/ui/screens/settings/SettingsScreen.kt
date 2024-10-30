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
package org.grating.recolldroid.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.ui.highlight
import org.grating.recolldroid.ui.model.RecollDroidViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsScreen(
    viewModel: RecollDroidViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value
    val lSet = uiState.liveSettings
    val pSet = uiState.pendingSettings.toBuilder()
    val fm = LocalFocusManager.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
           horizontalAlignment = Alignment.CenterHorizontally) {
        RecollAccountSettings(viewModel, pSet, fm)
        DownloadUrlRewriteSettings(viewModel, lSet, pSet, fm)
        DownloadAccountSettings(viewModel, lSet, pSet, fm)
    }
}

@Composable
fun SettingsTextField(
    label: String,
    value: String,
    dirty: Boolean,
    modifier: Modifier = Modifier,
    password: Boolean = false,
    onValueChange: (String) -> Unit = {},
) {
    var showPassword by remember { mutableStateOf(false) }
    val visualTransformation =
        if (password && !showPassword)
            PasswordVisualTransformation()
        else
            VisualTransformation.None
    OutlinedTextField(
        value = value,
        placeholder = { Text(text = label) },
        onValueChange = onValueChange,
        singleLine = true,
        shape = shapes.large,
        modifier = modifier.fillMaxWidth(),
        colors = if (dirty) {
            TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surfaceBright.highlight(Color.Red),
                unfocusedContainerColor = colorScheme.surface.highlight(Color.Red),
                disabledContainerColor = colorScheme.surfaceDim.highlight(Color.Red)
            )
        } else {
            TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surfaceBright,
                unfocusedContainerColor = colorScheme.surface,
                disabledContainerColor = colorScheme.surfaceDim
            )
        },
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (password) {
                OutlinedIconButton(
                    onClick = { showPassword = !showPassword }
                ) {
                    if (showPassword) {
                        Icon(imageVector = Icons.Filled.Visibility,
                             contentDescription = "Visible")
                    } else {
                        Icon(imageVector = Icons.Filled.VisibilityOff,
                             contentDescription = "Not visible")
                    }
                }
            }
        }
    )
}

@Composable
fun SaveRevertButtons(
    viewModel: RecollDroidViewModel,
    fm: FocusManager,
    isEnabled: () -> Boolean
) {
    Row {
        OutlinedButton(modifier = Modifier.padding(4.dp),
                       onClick = {
                           viewModel.commitPendingSettings()
                           fm.clearFocus()
                       },
                       enabled = isEnabled()
        ) {
            Text(text = "Save", maxLines = 1)
        }
        OutlinedButton(modifier = Modifier.padding(4.dp),
                       onClick = {
                           viewModel.revertPendingSettings()
                           fm.clearFocus()
                       },
                       enabled = isEnabled()
        ) {
            Text(text = "Revert", maxLines = 1)
        }
    }
}