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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.ui.data.RecollDroidAccount
import org.grating.recolldroid.ui.data.RecollDroidSettings
import org.grating.recolldroid.ui.model.RecollDroidViewModel

@Composable
fun RecollAccountSettings(
    viewModel: RecollDroidViewModel,
    pSet: RecollDroidSettings.Builder,
    fm: FocusManager
) {
    val uiState = viewModel.uiState.collectAsState().value
    val lAcc = uiState.liveSettings.recollAccount
    val pAcc = uiState.pendingSettings.recollAccount.toBuilder()
    val modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "RecollDroid Account",
                 style = MaterialTheme.typography.headlineSmall,
                 textAlign = TextAlign.Center)
            SettingsTextField(
                label = "Base URL",
                value = pAcc.baseUrl,
                dirty = lAcc.baseUrl != pAcc.baseUrl,
                modifier = modifier
            ) {
                viewModel.updatePendingSettings(
                    pSet.setRecollAccount(
                        pAcc.setBaseUrl(it).build()
                    ).build())
            }
            SettingsTextField(
                label = "Username",
                value = pAcc.username,
                dirty = lAcc.username != pAcc.username,
                modifier = modifier) {
                viewModel.updatePendingSettings(
                    pSet.setRecollAccount(
                        pAcc.setUsername(it).build()
                    ).build())
            }
            SettingsTextField(
                label = "Password",
                value = pAcc.password,
                dirty = lAcc.password != pAcc.password,
                password = true,
                modifier = modifier) {
                viewModel.updatePendingSettings(
                    pSet.setRecollAccount(
                        pAcc.setPassword(it).build()
                    ).build())
            }



            SaveRevertButtons(viewModel, fm) { !lAcc.equals(pAcc.build()) }
        }
    }
}