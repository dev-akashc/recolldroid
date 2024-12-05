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
package org.grating.recolldroid.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.grating.recolldroid.ui.logError

class RecollPagingSource(
    private val query: String,
    private val resultsRepository: ResultsRepository
) : PagingSource<Int, RecollSearchResult>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecollSearchResult> {
        try {
            val startIdx = params.key ?: 0
            val loadSize = params.loadSize
            val lastIdx = startIdx + loadSize - 1

            val rs = resultsRepository.executeQuery(query, startIdx, startIdx + params.loadSize - 1)

            if (rs.error.isNotBlank())
                throw RecollSearchException(rs.error)

            return LoadResult.Page(
                data = rs.page,
                prevKey = when {
                    startIdx == 0 -> null
                    else -> (startIdx - loadSize).coerceIn(0, rs.size - 1)
                },
                nextKey = when {
                    lastIdx >= rs.size -> null
                    else -> (lastIdx + 1).coerceIn(0, rs.size - 1)
                }
            )
        } catch(e : Exception) {
            logError("Error while calling load(${params}).", e)
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RecollSearchResult>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val result = state.closestItemToPosition(anchorPosition) ?: return null
        return result.idx - state.config.pageSize / 2
    }
}

class RecollSearchException(msg: String): Exception(msg)