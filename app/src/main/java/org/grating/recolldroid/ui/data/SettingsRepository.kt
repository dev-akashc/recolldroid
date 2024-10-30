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

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import org.grating.recolldroid.ui.logError
import java.io.IOException

interface SettingsRepository {
    val settingsFlow: Flow<RecollDroidSettings>
    suspend fun updateSettings(settings: RecollDroidSettings)
}


class DsSettingsRepository(
    private val settingsStore: DataStore<RecollDroidSettings>
) : SettingsRepository {
    override val settingsFlow: Flow<RecollDroidSettings> = settingsStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                logError("Error reading sort order preferences.", exception)
                emit(RecollDroidSettings.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun updateSettings(settings: RecollDroidSettings) {
        settingsStore.updateData { settings }
    }
}