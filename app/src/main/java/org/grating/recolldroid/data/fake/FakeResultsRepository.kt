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
package org.grating.recolldroid.data.fake

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.grating.recolldroid.data.RecollDocumentExtract
import org.grating.recolldroid.data.RecollSearchResult
import org.grating.recolldroid.data.RecollSnippet
import org.grating.recolldroid.data.ResultPreview
import org.grating.recolldroid.data.ResultSet
import org.grating.recolldroid.data.ResultsRepository

class FakeResultsRepository : ResultsRepository {
    override suspend fun executeQuery(query: String, first: Int, last: Int): ResultSet {
        return ResultSet(query = query,
                         size = 20,
                         queryMs = 20,
                         retrievalMs = 25,
                         firstIdx = 0,
                         lastIdx = 20,
                         page = FakeResultsDataProvider.getSampleResults())
    }

    override fun executeQuery(query: String): Flow<PagingData<RecollSearchResult>> {
        return emptyFlow()
    }

    override suspend fun retrievePreview(result: RecollSearchResult): ResultPreview {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveSnippets(result: RecollSearchResult): List<RecollSnippet> {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveExtract(result: RecollSearchResult): RecollDocumentExtract {
        TODO("Not yet implemented")
    }
}