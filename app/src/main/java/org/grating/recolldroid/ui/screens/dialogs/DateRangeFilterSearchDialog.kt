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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import org.grating.recolldroid.R
import org.grating.recolldroid.ui.DATE_FORMATTER
import org.grating.recolldroid.ui.convertMillisToDate
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.toEpochMillis
import java.time.LocalDate

/**
 * Dialog for restricting search results to some specified date range.
 */
@Composable
fun DateRangeFilterSearchDialog(
    viewModel: RecollDroidViewModel,
    message: AnnotatedString,
    range: Pair<LocalDate, LocalDate>,
    onSetDateRangeInclusive: (Pair<LocalDate, LocalDate>) -> Unit,
    onDismissRequest: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    var startDate: LocalDate by remember { mutableStateOf(range.first) }
    var endDate: LocalDate by remember { mutableStateOf(range.second) }
    var errorMsg: String by remember { mutableStateOf("") }

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
                painter = painterResource(R.drawable.date_time),
                contentDescription = "Filter by date range.",
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )
            Text(text = message)
            Text(text = errorMsg, color = Color.Red)
            Row {
                DateField(
                    label = "Start",
                    value = range.first,
                    onDateChanged = { startDate = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .weight(1f))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                     contentDescription = "thru",
                     modifier = Modifier.align(Alignment.CenterVertically))
                DateField(
                    label = "End",
                    value = range.second,
                    onDateChanged = { endDate = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .weight(1f))
            }
            Row {
                TextButton(
                    onClick = {
                        errorMsg = when {
                            startDate.isAfter(endDate) -> "Start date must come before or on end date."
                            else -> ""
                        }
                        if (errorMsg.isBlank()) {
                            onSetDateRangeInclusive(Pair(startDate, endDate))
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(stringResource(R.string.set_date_range_filter_action))
                }
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier
                ) {
                    Text(stringResource(R.string.cancel_action))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    value: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(value.toEpochMillis())
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = datePickerState.selectedDateMillis?.convertMillisToDate() ?: ""
    var prevSelectedDate: String by remember { mutableStateOf(selectedDate) }

    if (showDatePicker && selectedDate != prevSelectedDate) {
        onDateChanged(LocalDate.parse(selectedDate, DATE_FORMATTER))
        showDatePicker = false
        prevSelectedDate = selectedDate
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        },
        modifier = modifier
    )

    if (showDatePicker) {
        Popup(
            onDismissRequest = { showDatePicker = false },
            alignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 64.dp)
                    .shadow(elevation = 4.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
