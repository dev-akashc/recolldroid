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
package org.grating.recolldroid.ui.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import org.grating.recolldroid.ui.logError
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<RecollDroidSettings> {
    override val defaultValue: RecollDroidSettings = RecollDroidSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): RecollDroidSettings {
        try {
            return RecollDroidSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            logError("Cannot read proto.", exception)
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: RecollDroidSettings, output: OutputStream) = t.writeTo(output)
}