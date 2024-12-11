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

import android.app.DownloadManager
import android.app.DownloadManager.Query
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import org.grating.recolldroid.RecollDroidApplication
import org.grating.recolldroid.DownloadedDocumentDetail
import org.grating.recolldroid.network.HttpException
import org.grating.recolldroid.network.httpReason

class DownloadCompletedReceiver : BroadcastReceiver() {

    private lateinit var downloadManager: DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {
        downloadManager = context?.getSystemService(DownloadManager::class.java)!!
        logInfo(intent?.toString() ?: "No intent")
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            val cursor: Cursor = downloadManager.query(Query().setFilterById(id))
            if (cursor.moveToFirst() && cursor.count > 0) {
                val status = cursor.getString(DownloadManager.COLUMN_STATUS).httpReason()
                logInfo(status)
                if (status != "Ok") {
                    logError("Failed to download document: $status")
                    (context.applicationContext as RecollDroidApplication).container.signalError(
                        "Download failed.", HttpException(status)
                    )
                } else {
                    try {
                        (context.applicationContext as RecollDroidApplication).container.signalDocumentArrived(
                            DownloadedDocumentDetail(Uri.parse(cursor.getString(DownloadManager.COLUMN_LOCAL_URI)),
                                                     cursor.getString(DownloadManager.COLUMN_MEDIA_TYPE))
                        )
                    } catch (ex: Throwable) {
                        (context.applicationContext as RecollDroidApplication).container.signalError(
                            "Failed to open document.", ex
                        )
                    }
                }
            }
        }
    }
}

class MissingResponseDataException(msg: String) : Exception(msg)

fun Cursor.getString(colName: String): String {
    val idx = getColumnIndex(colName)
    if (idx < 0)
        throw MissingResponseDataException("$colName not in response data.  That's unexpected :-/")
    else
        return getString(idx) ?: "$colName is null"
}
