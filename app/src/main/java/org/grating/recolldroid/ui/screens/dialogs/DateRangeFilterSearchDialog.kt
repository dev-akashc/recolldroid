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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.R
import org.grating.recolldroid.ui.DATE_FORMATTER
import org.grating.recolldroid.ui.DATE_PATTERN
import org.grating.recolldroid.ui.highlight
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.testFormat
import org.grating.recolldroid.ui.toLocalDate
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
    var startDate: LocalDate? by remember { mutableStateOf(range.first) }
    var endDate: LocalDate? by remember { mutableStateOf(range.second) }
    var error: String by remember { mutableStateOf("") }

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
            Text(text = error, color = colorScheme.error)
            DateField(
                label = "Start",
                value = range.first,
                onDateChanged = { startDate = it; error = testDates(startDate, endDate) },
                modifier = Modifier
            )
            DateField(
                label = "End",
                value = range.second,
                onDateChanged = { endDate = it; error = testDates(startDate, endDate) },
                modifier = Modifier
            )
            Row {
                TextButton(
                    enabled = startDate != null && endDate != null && !startDate!!.isAfter(endDate),
                    onClick = {
                        onSetDateRangeInclusive(Pair(startDate!!, endDate!!))
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

fun testDates(start: LocalDate?, end: LocalDate?): String {
    return when {
        start == null && end == null -> "Bad start and end dates."
        start == null -> "Bad start date."
        end == null -> "Bad end date."
        start.isAfter(end) -> "End must come on or after start."
        else -> ""
    }
}

@Composable
fun DateField(
    label: String,
    value: LocalDate,
    onDateChanged: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate: String by remember { mutableStateOf(value.format(DATE_FORMATTER)) }
    var error by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {
                selectedDate = it
                error = it.testFormat()
                if (error.isBlank())
                    onDateChanged(it.toLocalDate())
                else
                    onDateChanged(null)
            },
            label = { Text(label) },
            placeholder = { Text(DATE_PATTERN) },
            readOnly = false,
            modifier = modifier,
            colors = if (error.isNotBlank()) {
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
            }
        )
        Text(text = error, color = colorScheme.error)
    }
}
