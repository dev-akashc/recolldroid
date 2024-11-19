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
package org.grating.recolldroid.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import org.grating.recolldroid.R
import org.grating.recolldroid.ui.model.QueryFragment
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.min

private const val KB: Long = 1024
private const val MB: Long = KB * KB
private const val GB: Long = MB * KB
private const val TB: Long = GB * KB

const val SEARCH_HIST_SZ = 100
const val SEARCH_HIST_DROPDOWN_SZ = 10

fun Long.readableFileSize(): String {
    return when {
        this < KB -> "$this b"
        this < MB -> "${this / KB} Kb"
        this < GB -> "${this / MB} Mb"
        this < TB -> "${this / GB} Gb"
        else -> "${this / TB} Tb"
    }
}

infix fun String.or(other: String): String {
    return this.ifBlank { other }
}

const val START_HIGHLIGHT = "†"
const val END_HIGHLIGHT = "‡"
fun String.doHighlight(): AnnotatedString {
    val str = this
    return buildAnnotatedString {
        var highlighting = false
        str.split(START_HIGHLIGHT, END_HIGHLIGHT).forEach {
            if (highlighting)
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append(it)
                }
            else
                append(it)
            highlighting = !highlighting
        }
    }
}

@Composable
fun LoadingSpinner() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .size(96.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(96.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

    }
}

@Composable
fun LoadingError(errMsg: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.error),
            contentDescription = "default_loading_error",
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp)
        )
        Text(text = errMsg, Modifier.padding(8.dp))
    }
}

fun LazyListScope.addLoadState(state: LoadState) {
    when (state) {
        LoadState.Loading -> item {
            LoadingSpinner()
        }

        is LoadState.Error -> item {
            LoadingError(state.error.message ?: state.error.toString())
        }

        is LoadState.NotLoading -> {} // Nothing to show.
    }
}

fun Any.logError(msg: Any, e: Throwable) {
    Log.e(javaClass.simpleName, msg.toString(), e)
}

fun Any.logError(msg: Any) {
    Log.e(javaClass.simpleName, msg.toString())
}

fun Any.logInfo(msg: Any) {
    Log.i(javaClass.simpleName, msg.toString())
}

/**
 * Highlight a colour by first ensuring the alpha isn't completely opaque and then rendering over
 * some specified colour.
 * @param c Color to highlight with.
 * @return the highlighted colour.
 */
fun Color.highlight(c: Color): Color {
    return Color(this.red, this.green, this.blue, 0.8f).compositeOver(c)
}

fun String.cleanup(): String {
    return replace("<br>", "")
        .replace("&amp;", "&")
        .replace("&nbsp;", " ")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
}

private val PLUS_MIME_MATCH = "[^-]mime:\\S+".toRegex()
private val MINUS_MIME_MATCH = "-mime:\\S+".toRegex()
private val WHITESPACE = "\\s+".toRegex()
fun String.removeMinusMimes(): String {
    return replace(MINUS_MIME_MATCH, "")
        .replace(WHITESPACE, " ")
        .trim()
}

fun String.removePlusMimes(): String {
    return replace(PLUS_MIME_MATCH, "")
        .replace(WHITESPACE, " ")
        .trim()
}

fun <E> MutableList<E>.prepend(e: E): MutableList<E> {
    this.add(0, e)
    return this
}

const val DATE_PATTERN = "yyyy-MM-dd"
val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
val DATE_RANGE_MATCHER = """\bdate:(\d\d\d\d-\d\d-\d\d/\d\d\d\d-\d\d-\d\d)\b""".toRegex()
val DATE_RANGE_MATCHER2 = (".*" + DATE_RANGE_MATCHER.toPattern() + ".*").toRegex()
fun Long.secondsToLocalDate(): LocalDate {
    return ofInstant(Instant.ofEpochMilli(this * 1000), ZoneId.systemDefault())
        .toLocalDate()
}

val EPOCH_DATE: LocalDate = LocalDate.of(1970, 1, 1)
val DEFAULT_DATE_RANGE = Pair(EPOCH_DATE, LocalDate.now())

fun getDateRange(queryStr: String): Pair<LocalDate, LocalDate>? {
    if (queryStr.matches(DATE_RANGE_MATCHER2)) {
        val dateRangeStr = queryStr.replace(DATE_RANGE_MATCHER2, "$1")
        return Pair(LocalDate.parse(dateRangeStr.substring(0, 10), DATE_FORMATTER),
                    LocalDate.parse(dateRangeStr.substring(11, 21), DATE_FORMATTER))
    } else
        return null
}

fun LocalDate.toEpochMillis(): Long {
    // Assume time is 00:00 in system default timezone.
    return atTime(0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun LocalDate.toDateString(): String {
    return DATE_FORMATTER.format(this)
}

fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this, DATE_FORMATTER)
}

fun String.testFormat(): String {
    return when {
        isBlank() -> "Not set"
        else ->
            try {
                LocalDate.parse(this, DATE_FORMATTER)
                return ""
            } catch (e: DateTimeParseException) {
                "Must be $DATE_PATTERN"
            } catch (t: Throwable) {
                t.toString()
            }
    }
}

fun Pair<LocalDate, LocalDate>.toRecollDateRangeString(): String {
    return "date:${first.toDateString()}/${second.toDateString()}"
}

/**
 * Return a sublist containing the first 'n' items from the original list.  If the original list is
 * smaller than 'n', then return the list itself.
 */
fun <T> List<T>.firstN(n: Int): List<T> {
    return subList(0, min(n, size))
}

/**
 * Get the query fragment (contiguous non-whitespace chars) located at the specified position.
 */
fun String.getQueryFragment(pos: Int): QueryFragment {
    if (isEmpty()) return QueryFragment.EMPTY
    var start: Int = pos
    while (start in indices && !this[start].isWhitespace()) --start
    if (start == pos) return QueryFragment.EMPTY
    var end: Int = pos
    while (end in indices && !this[end].isWhitespace()) end++
    return QueryFragment(substring(start + 1, end), start + 1)
}

fun TextFieldValue.getQueryFragment(): QueryFragment {
    return text.getQueryFragment(selection.start)
}

fun QueryFragment.isDateRangeFilter(): Boolean {
    return word.startsWith("date:")
}

fun QueryFragment.isMimeTypeFilter(): Boolean {
    return word.startsWith("mime:") || word.startsWith("-mime:")
}

fun QueryFragment.getMimeType(): String {
    return when {
        word.startsWith("mime:") -> word.substring(5)
        word.startsWith("-mime:") -> word.substring(6)
        else -> ""
    }
}

fun String.replace(qf: QueryFragment, sub: String) =
    replaceRange(qf.pos, qf.pos + qf.word.length, sub)
