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

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.R
import org.grating.recolldroid.ui.data.removePastSearchAt
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.simpleVerticalScrollbar

@Composable
fun SearchHistoryScreen(
    viewModel: RecollDroidViewModel,
    onHistoricalSearchClick: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .simpleVerticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        uiState.liveSettings.pastSearchList.forEachIndexed { idx, qStr ->
            Row(modifier = Modifier
                .padding(2.dp)
                .border(1.dp, MaterialTheme.colorScheme.primaryContainer)) {
                Text(text = qStr,
                     modifier =
                     Modifier
                         .weight(1f)
                         .padding(4.dp)
                         .clickable(onClick = {
                             onHistoricalSearchClick(qStr)
                         }))
                OutlinedIconButton(
                    onClick = {
                        viewModel.updatePendingSettings(
                            uiState.pendingSettings.toBuilder()
                                .removePastSearchAt(idx).build()
                        ).commitPendingSettings()
                    },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Delete,
                         contentDescription = stringResource(R.string.delete_action))
                }
            }
        }
    }
}

