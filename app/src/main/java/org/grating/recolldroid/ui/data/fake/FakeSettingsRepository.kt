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
package org.grating.recolldroid.ui.data.fake

import kotlinx.coroutines.flow.Flow
import org.grating.recolldroid.ui.data.RecollDroidAccount
import org.grating.recolldroid.ui.data.RecollDroidSettings
import org.grating.recolldroid.ui.data.SettingsRepository

class FakeSettingsRepository : SettingsRepository {
    override val settingsFlow: Flow<RecollDroidSettings>
        get() = TODO("Not yet implemented")

    override suspend fun updateSettings(settings: RecollDroidSettings) {
        TODO("Not yet implemented")
    }
}