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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Scrollbar for lazy lists that bases its sizing and position on the average element size that has
 * been observed in the list so far, together with the (hopefully) known total number of items in
 * the list and the currently viewed item at the top of the viewport.
 */
@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    coreSize: Int,
    width: Dp = 8.dp
): Modifier {

    if (coreSize == 0) return this

    // Scrollbar Fade in/out when scrolling (shamelessly stolen from the internet)
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration), label = ""
    )

    val avgPageHeight = remember { Avg(coreSize) }
    val sbColor = MaterialTheme.colorScheme.primary
    return drawWithContent {
        drawContent()

        state.layoutInfo.visibleItemsInfo.forEach {
            avgPageHeight.push(it.size.toFloat(), it.index)
        }

        if (state.isScrollInProgress) {
            val aph = avgPageHeight.value // Average height of pages seen so far.
            val li = state.layoutInfo
            val max = (coreSize - li.visibleItemsInfo.size) * aph // max scroll position (est) in pixels.
            val vpt = li.viewportSize.height.toFloat()
            val sbh = (vpt * vpt) / (max + vpt)
            val pos = li.visibleItemsInfo[0].index.toFloat() * aph // scroll position (est) in pixels.
            val sby = pos / max * (vpt - sbh)
            drawRoundRect(
                color = sbColor,
                cornerRadius = CornerRadius(15f, 15f),
                topLeft = Offset(size.width - width.toPx(), sby),
                size = Size(width.toPx(), sbh),
                alpha = alpha
            )
        }
    }
}

@Composable
fun Modifier.simpleVerticalScrollbar(
    state: ScrollState,
    width: Dp = 8.dp
): Modifier {
    // Scrollbar Fade in/out when scrolling (shamelessly stolen from the internet)
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration), label = ""
    )

    val sbColor = MaterialTheme.colorScheme.primary
    return drawWithContent {
        drawContent()
        val max = state.maxValue.toFloat()
        val vpt = state.viewportSize.toFloat()
        val sbh = (vpt * vpt) / (vpt + max)
        val pos = state.value.toFloat()
        if (state.isScrollInProgress) {
            val sby = pos / max * (vpt - sbh)
            drawRoundRect(
                color = sbColor,
                cornerRadius = CornerRadius(15f, 15f),
                topLeft = Offset(size.width - width.toPx(), sby),
                size = Size(width.toPx(), sbh),
                alpha = alpha
            )
        }
    }
}

/**
 * Calculate an arithmetic average of `n` items.
 * @param n number of items we expect to see.
 */
class Avg(n: Int) {
    private val seen = MutableList<Boolean>(n) { false }
    private var count = 0
    private var sum = 0f

    /**
     * Record a new observation at position `idx`, iff no observation has been seen at that position
     * before.
     *
     * @param x the observation.
     * @param idx the observation's position (or id, if you prefer).
     */
    fun push(x: Float, idx: Int) {
        if (!seen[idx]) {
            count++
            sum += x
            seen[idx] = true
        }
    }

    /**
     * The current average, based on the observations that have been made so far.
     */
    val value: Float
        get() = sum / count
}
