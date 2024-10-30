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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.ui.data.DownloadAccount
import org.grating.recolldroid.ui.data.RecollDroidSettings
import org.grating.recolldroid.ui.model.RecollDroidViewModel

@Composable
fun DownloadAccountSettings(
    viewModel: RecollDroidViewModel,
    lSet: RecollDroidSettings,
    pSet: RecollDroidSettings.Builder,
    fm: FocusManager
) {
    val uiState = viewModel.uiState.collectAsState().value

    val pAccs = pSet.downloadAccountList
    val lAccs = lSet.downloadAccountList
    val modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
               modifier = Modifier.fillMaxWidth()) {
            Row {
                Spacer(modifier = Modifier.size(24.dp))
                Text(text = "Download Accounts",
                     style = MaterialTheme.typography.headlineSmall,
                     textAlign = TextAlign.Center,
                     modifier = Modifier.weight(1f)
                )
                OutlinedIconButton(
                    onClick = {
                        viewModel.updatePendingSettings(
                            pSet.addDownloadAccount(
                                DownloadAccount.newBuilder().build()
                            ).build())
                    },
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(4.dp)
                        .size(24.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Add,
                         contentDescription = "Add New Rewrite Rule")
                }
            }
            pSet.downloadAccountList.forEachIndexed { idx, account ->
                if (idx > 0) {
                    HorizontalDivider(thickness = 2.dp,
                                      modifier = Modifier.padding(start=24.dp, end=24.dp))
                }
                Row {
                    Column(modifier = Modifier.weight(1f).padding(bottom=8.dp)) {
                        SettingsTextField(
                            label = "Base URL",
                            value = account.baseUrl,
                            dirty = lAccs.getOrNull(idx)?.baseUrl != pAccs[idx].baseUrl,
                            modifier = Modifier
                                .padding(start = 8.dp)
                        ) {
                            viewModel.updatePendingSettings(
                                pSet.setDownloadAccount(
                                    idx,
                                    pSet.downloadAccountList[idx].toBuilder().setBaseUrl(it)
                                        .build()
                                ).build()
                            )
                        }
                        SettingsTextField(
                            label = "Username",
                            value = account.username,
                            dirty = lAccs.getOrNull(idx)?.username != pAccs[idx].username,
                            modifier = Modifier
                                .padding(start = 8.dp
                                )

                        ) {
                            viewModel.updatePendingSettings(
                                pSet.setDownloadAccount(
                                    idx,
                                    pSet.downloadAccountList[idx].toBuilder().setUsername(it)
                                        .build()
                                ).build()
                            )
                        }
                        SettingsTextField(
                            label = "Password",
                            value = account.password,
                            dirty = lAccs.getOrNull(idx)?.password != pAccs[idx].password,
                            password = true,
                            modifier = Modifier
                                .padding(start = 8.dp)
                        ) {
                            viewModel.updatePendingSettings(
                                pSet.setDownloadAccount(
                                    idx,
                                    pSet.downloadAccountList[idx].toBuilder().setPassword(it)
                                        .build()
                                ).build()
                            )
                        }
                    }
                    OutlinedIconButton(
                        onClick = {
                            viewModel.updatePendingSettings(
                                pSet.removeDownloadAccount(idx).build())
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(4.dp)
                            .size(24.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Delete,
                             contentDescription = "Delete Download Account")
                    }
                }
            }
            Row {
                SaveRevertButtons(viewModel, fm) { !lAccs.equals(pAccs) }
            }
        }
    }
}
