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
package org.grating.recolldroid.network

import org.grating.recolldroid.data.RecollApiClient
import org.grating.recolldroid.data.RecollDocumentExtract
import org.grating.recolldroid.data.RecollSnippets
import org.grating.recolldroid.data.ResultPreview
import org.grating.recolldroid.data.ResultSet
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApiClient : RecollApiClient {
    @GET("search")
    override suspend fun search(
        @Query("query_str") query: String,
        @Query("first") first: Int,
        @Query("last") last: Int
    ): ResultSet

    @GET("preview")
    override suspend fun preview(
        @Query("query_str") query: String,
        @Query("idx") idx: Int,
    ): ResultPreview

    @GET("snippets")
    override suspend fun snippets(
        @Query("query_str") query: String,
        @Query("idx") idx: Int,
    ): RecollSnippets

    @GET("extract")
    override suspend fun extract(
        @Query("query_str") query: String,
        @Query("idx") idx: Int,
    ): RecollDocumentExtract
}