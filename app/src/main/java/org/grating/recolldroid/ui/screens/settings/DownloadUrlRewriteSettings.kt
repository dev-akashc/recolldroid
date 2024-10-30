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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
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
import org.grating.recolldroid.ui.data.DocumentLinkRewrite
import org.grating.recolldroid.ui.data.RecollDroidSettings
import org.grating.recolldroid.ui.model.RecollDroidViewModel

@Composable
fun DownloadUrlRewriteSettings(
    viewModel: RecollDroidViewModel,
    lSet: RecollDroidSettings,
    pSet: RecollDroidSettings.Builder,
    fm: FocusManager
) {
    val uiState = viewModel.uiState.collectAsState().value

    val pRewrites = pSet.rewriteList
    val lRewrites = lSet.rewriteList

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            .fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
               modifier = Modifier.fillMaxWidth()) {
            Row {
                Spacer(modifier = Modifier.size(24.dp))
                Text(text = "Download Url Rewrites",
                     style = MaterialTheme.typography.headlineSmall,
                     textAlign = TextAlign.Center,
                     modifier = Modifier.weight(1f)
                )
                OutlinedIconButton(
                    onClick = {
                        viewModel.updatePendingSettings(
                            pSet.addRewrite(
                                DocumentLinkRewrite.newBuilder().build()
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
            pSet.rewriteList.forEachIndexed { idx, rewrite ->
                if (idx > 0) {
                    HorizontalDivider(thickness = 2.dp,
                                      modifier = Modifier.padding(start=24.dp, end=24.dp))
                }
                Row {
                    Column(modifier = Modifier.weight(1f).padding(bottom=8.dp)) {
                        SettingsTextField(
                            label = "Search",
                            value = rewrite.search,
                            dirty = lRewrites.getOrNull(idx)?.search != pRewrites[idx].search,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            viewModel.updatePendingSettings(
                                pSet.setRewrite(
                                    idx,
                                    pSet.rewriteList[idx].toBuilder().setSearch(it).build()
                                ).build()
                            )
                        }
                        SettingsTextField(
                            label = "Replace",
                            value = rewrite.replace,
                            dirty = lRewrites.getOrNull(idx)?.replace != pRewrites[idx].replace,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            viewModel.updatePendingSettings(
                                pSet.setRewrite(
                                    idx,
                                    pSet.rewriteList[idx].toBuilder().setReplace(it).build()
                                ).build()
                            )
                        }
                    }
                    OutlinedIconButton(
                        onClick = {
                            viewModel.updatePendingSettings(pSet.removeRewrite(idx).build())
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(4.dp, end = 8.dp)
                            .size(24.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Delete,
                             contentDescription = "Add New Rewrite Rule")
                    }

                }
            }
            Row {
                SaveRevertButtons(viewModel, fm) { !lRewrites.equals(pRewrites) }
            }
        }
    }
}

