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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.grating.recolldroid.ui.logInfo

interface ResultsRepository {
    fun executeQuery(query: String): Flow<PagingData<RecollSearchResult>>
    suspend fun executeQuery(query: String, first: Int, last: Int): ResultSet
    suspend fun retrievePreview(result: RecollSearchResult): ResultPreview
    suspend fun retrieveSnippets(result: RecollSearchResult): List<RecollSnippet>
    suspend fun retrieveExtract(result: RecollSearchResult): RecollDocumentExtract
}

object NullRepository : ResultsRepository {
    override fun executeQuery(query: String): Flow<PagingData<RecollSearchResult>> {
        return emptyFlow()
    }

    override suspend fun executeQuery(query: String, first: Int, last: Int): ResultSet {
        return ResultSet.NULL
    }

    override suspend fun retrievePreview(result: RecollSearchResult): ResultPreview {
        return ResultPreview.NOT_LOADED
    }

    override suspend fun retrieveSnippets(result: RecollSearchResult): List<RecollSnippet> {
        return emptyList()
    }

    override suspend fun retrieveExtract(result: RecollSearchResult): RecollDocumentExtract {
        return RecollDocumentExtract.NULL
    }
}

class DefaultResultsRepository(private var recollApiClient: RecollApiClient) :
    ResultsRepository {

    fun resetRecollApiClient(recollApiClient: RecollApiClient) {
        this.recollApiClient = recollApiClient
    }

    override fun executeQuery(query: String): Flow<PagingData<RecollSearchResult>> {
        return Pager(
            config = PagingConfig(initialLoadSize = 10, pageSize = 10, prefetchDistance = 2),
            pagingSourceFactory = {
                RecollPagingSource(query, this)
            }).flow
    }

    override suspend fun executeQuery(query: String, first: Int, last: Int): ResultSet {
        val rs = recollApiClient.search(query, first, last)
        var idx = rs.firstIdx
        rs.page.forEach {
            it.resultSet = rs
            it.idx = idx++
        }
        return rs
    }

    override suspend fun retrievePreview(result: RecollSearchResult): ResultPreview {
        if (result.preview == ResultPreview.NOT_LOADED) {
            result.preview = recollApiClient.preview(result.resultSet.query, result.idx)
        } else {
            logInfo("Preview already loaded.")
        }
        return result.preview
    }

    override suspend fun retrieveSnippets(result: RecollSearchResult): List<RecollSnippet> {
        if (result.snippetsList == RecollSearchResult.NULL_SNIPPETS) {
            result.snippetsList =
                recollApiClient.snippets(result.resultSet.query, result.idx).snippets
        } else {
            logInfo("Snippets already loaded.")
        }
        return result.snippetsList
    }

    override suspend fun retrieveExtract(result: RecollSearchResult): RecollDocumentExtract {
        return recollApiClient.extract(result.resultSet.query, result.idx)
    }
}