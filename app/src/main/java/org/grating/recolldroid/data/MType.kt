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

import org.grating.recolldroid.R

enum class DocType(val text: String) {
    HTML("text/html"),
    PDF("application/pdf"),
    DJVU("image/vnd.djvu"),
    SUBTITLE("text/x-srt"),
    POSTSCRIPT("application/postscript"),
    AUDIO_MPG("audio/mpeg"),
    AUDIO_FLAC("application/x-flac"),
    XML("application/xml"),
    EPUB("application/epub+zip"),
    DIR("inode/directory"),
    VIDEO_MATROSKA("video/x-matroska"),
    EMAIL("message/rfc822"),

    VIDEO_UNKNOWN("video/"),
    AUDIO_UNKNOWN("audio/"),

    UNKNOWN("???");

    val typeIcon: Int
        get() = when (this) {
            HTML -> R.drawable.html
            PDF -> R.drawable.pdf
            DJVU -> R.drawable.book
            SUBTITLE -> R.drawable.txt
            POSTSCRIPT -> R.drawable.postscript
            AUDIO_MPG -> R.drawable.sownd // (sic)
            AUDIO_FLAC -> R.drawable.sownd // (sic)
            XML -> R.drawable.xml
            EPUB -> R.drawable.epub_zip
            DIR -> R.drawable.folder
            VIDEO_MATROSKA -> R.drawable.video
            EMAIL -> R.drawable.message

            VIDEO_UNKNOWN -> R.drawable.video
            AUDIO_UNKNOWN -> R.drawable.sownd

            UNKNOWN -> R.drawable.unknown_file_type
        }

    companion object {
        private val BY_TEXT = DocType.entries.associateBy({ it.text }, { it }) // <3

        fun byText(txt: String): DocType {
            return BY_TEXT[txt] ?: when {
                txt.startsWith(VIDEO_UNKNOWN.text) -> VIDEO_UNKNOWN
                txt.startsWith(VIDEO_UNKNOWN.text) -> VIDEO_UNKNOWN
                else -> UNKNOWN
            }
        }
    }
}

data class MType(val docType: DocType, val rawType: String) {
    override fun toString(): String {
        return when (docType) {
            DocType.UNKNOWN -> rawType
            else -> {
                docType.toString()
            }
        }
    }

    companion object {
        val UNKNOWN: MType = MType(DocType.UNKNOWN, "UNKNOWN")
    }
}