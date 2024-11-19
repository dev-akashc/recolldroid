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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.grating.recolldroid.ui.secondsToLocalDate
import java.net.URL
import java.time.LocalDate

@Serializable
data class ResultSet(
    /** The query string to pass to recoll when searching */
    @SerialName(value = "query_str")
    val query: String,

    /** Total number of results returned by query */
    @SerialName(value = "n_results")
    val size: Int,

    /** Time taken to perform query in milliseconds. */
    @SerialName(value = "query_ms")
    val queryMs: Int,

    /** Time take to retrieve page in milliseconds. */
    @SerialName(value = "retrieval_ms")
    val retrievalMs: Int,

    /** Index (relative to full list of documents returned by associated query) of first document in page. */
    @SerialName(value = "first")
    val firstIdx: Int,

    /** Index (relative to full list of documents returned by associated query) of last document in page. */
    @SerialName(value = "last")
    val lastIdx: Int,

    /** Page (or subset) of documents selected by the associated query. */
    @SerialName(value = "results")
    val page: List<RecollSearchResult>
) {
    companion object {
        val NULL: ResultSet = ResultSet("No Query",
                                        0,
                                        0,
                                        0,
                                        -1,
                                        -1,
                                        emptyList())
    }
}

/**
 * Encapsulate all fields returned by Recoll API server for a single search result.
 */
@Serializable
data class RecollSearchResult(
    @Serializable(with = UrlSerializer::class)
    val url: URL = UNKNOWN_URL,
    val sig: String = UNKNOWN_STR,
    @SerialName(value = "rclmbreaks")
    @Serializable(with = RecollMultiBreaks::class)
    val recollMultiBreaks: List<Pair<Long, Long>> = emptyList(),
    @SerialName(value = "ipath")
    val iPath: String = UNKNOWN_STR,
    @SerialName(value = "rcludi")
    val recollUdi: String = UNKNOWN_STR,
    val title: String = UNKNOWN_STR,
    @SerialName(value = "fbytes")
    val fBytes: Long = UNKNOWN_LNG,
    @SerialName(value = "collapsecount")
    val collapseCount: Int = 0,
    val abstract: String = UNKNOWN_STR,
    val recipient: String = UNKNOWN_STR,
    val author: String = UNKNOWN_STR,
    val caption: String = UNKNOWN_STR,
    @SerialName(value = "dbytes")
    val dBytes: Long = UNKNOWN_LNG,
    val filename: String = UNKNOWN_STR,
    @SerialName(value = "relevancyrating")
    @Serializable(with = RelevancyRatingSerializer::class)
    val relevancyRatingPercent: Double = UNKNOWN_DBL,
    @SerialName(value = "fmtime")
    val fmTime: Long = UNKNOWN_LNG,
    @SerialName(value = "mtype")
    @Serializable(with = MTypeSerializer::class)
    val mType: MType = MType.UNKNOWN,
    @SerialName(value = "origcharset")
    val origCharset: String = UNKNOWN_STR,
    @SerialName(value = "mtime")
    val mTime: Long = UNKNOWN_LNG,
    @SerialName(value = "pcbytes")
    val pcBytes: Long = UNKNOWN_LNG,
    @SerialName(value = "keywords")
    val keyWords: String = UNKNOWN_STR,
    @SerialName(value = "rclaptg")
    val recollAptg: String = UNKNOWN_STR,
    @SerialName(value = "dmtime")
    val dmTime: Long = UNKNOWN_LNG,
    @SerialName(value = "snippets_abstract")
    val snippetsAbstract: String = UNKNOWN_STR,
) {
    // Reference to containing result set.
    @Transient
    lateinit var resultSet: ResultSet

    // Used to store the index of this result within the total list of results produced by the
    // query that retrieved this result.
    @Transient
    var idx: Int = -1

    // Used to cache the preview - once requested - for this result .
    @Transient
    var preview: ResultPreview = ResultPreview.NOT_LOADED

    // Used to cache the snippets list - once requested - for this result.
    @Transient
    var snippetsList: List<RecollSnippet> = NULL_SNIPPETS

    val date: LocalDate
        get() = (if (dmTime > 0) dmTime else fmTime).secondsToLocalDate()

    companion object {
        const val UNKNOWN_STR: String = "UNKNOWN"
        const val UNKNOWN_DBL: Double = -1.0
        const val UNKNOWN_LNG: Long = -1
        const val EMPTY_STR: String = ""
        val UNKNOWN_URL: URL = URL("http://nope")
        val NULL_SNIPPETS: List<RecollSnippet> = emptyList()
        // val None = RecollSearchResult()
    }
}

@Serializable
data class RecollSnippets(
    val snippets: List<RecollSnippet>
)

@Serializable
data class RecollSnippet(
    @SerialName(value = "p")
    val page: Int,
    @SerialName(value = "kw")
    val keyWord: String,
    @SerialName(value = "s")
    val snippet: String
)

@Serializable
data class RecollDocumentExtract(
    val url: String,
    val msg: String
) {
    companion object {
        val NULL = RecollDocumentExtract("", "NULL")
    }
}

object UrlSerializer : KSerializer<URL> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("URL", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): URL {
        return URL(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: URL) {
        encoder.encodeString(value.toString())
    }
}

object MTypeSerializer : KSerializer<MType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("DocType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MType {
        val rawType = decoder.decodeString()
        return MType(docType = DocType.fromText(rawType), rawType)
    }

    override fun serialize(encoder: Encoder, value: MType) {
        encoder.encodeString(value.rawType)
    }
}

object RelevancyRatingSerializer : KSerializer<Double> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("RelevancyRating", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Double {
        return decoder.decodeString().trim().substringBefore('%').toDouble()
    }

    override fun serialize(encoder: Encoder, value: Double) {
        return encoder.encodeString("${value}%")
    }
}

object RecollMultiBreaks : KSerializer<List<Pair<Double, Double>>> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("RecollMultiBreaks", PrimitiveKind.STRING)

    // Oh man look how easy this is!?
    override fun deserialize(decoder: Decoder): List<Pair<Double, Double>> {
        return decoder.decodeString().split(',').chunked(2) { (first, second) ->
            Pair(first.toDouble(), second.toDouble())
        }
    }

    override fun serialize(encoder: Encoder, value: List<Pair<Double, Double>>) {
        return encoder.encodeString(value.joinToString {
            "${it.first},${it.second}"
        })
    }
}
