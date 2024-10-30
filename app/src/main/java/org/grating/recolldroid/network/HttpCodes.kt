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

fun String.httpReason():String {
    return when(this) {
        "200" -> "Ok"
        "201" -> "HTTP Status-Code 201: Created."
        "202" -> "HTTP Status-Code 202: Accepted."
        "203" -> "HTTP Status-Code 203: Non-Authoritative Information."
        "204" -> "HTTP Status-Code 204: No Content."
        "205" -> "HTTP Status-Code 205: Reset Content."
        "206" -> "HTTP Status-Code 206: Partial Content."
        "300" -> "HTTP Status-Code 300: Multiple Choices."
        "301" -> "HTTP Status-Code 301: Moved Permanently."
        "302" -> "HTTP Status-Code 302: Temporary Redirect."
        "303" -> "HTTP Status-Code 303: See Other."
        "304" -> "HTTP Status-Code 304: Not Modified."
        "305" -> "HTTP Status-Code 305: Use Proxy."
        "400" -> "HTTP Status-Code 400: Bad Request."
        "401" -> "HTTP Status-Code 401: Unauthorized."
        "402" -> "HTTP Status-Code 402: Payment Required."
        "403" -> "HTTP Status-Code 403: Forbidden."
        "404" -> "HTTP Status-Code 404: Not Found."
        "405" -> "HTTP Status-Code 405: Method Not Allowed."
        "406" -> "HTTP Status-Code 406: Not Acceptable."
        "407" -> "HTTP Status-Code 407: Proxy Authentication Required."
        "408" -> "HTTP Status-Code 408: Request Time-Out."
        "409" -> "HTTP Status-Code 409: Conflict."
        "410" -> "HTTP Status-Code 410: Gone."
        "411" -> "HTTP Status-Code 411: Length Required."
        "412" -> "HTTP Status-Code 412: Precondition Failed."
        "413" -> "HTTP Status-Code 413: Request Entity Too Large."
        "414" -> "HTTP Status-Code 414: Request-URI Too Large."
        "415" -> "HTTP Status-Code 415: Unsupported Media Type."
        "500" -> "HTTP Status-Code 500: Internal Server Error."
        "501" -> "HTTP Status-Code 501: Not Implemented."
        "502" -> "HTTP Status-Code 502: Bad Gateway."
        "503" -> "HTTP Status-Code 503: Service Unavailable."
        "504" -> "HTTP Status-Code 504: Gateway Timeout."
        "505" -> "HTTP Status-Code 505: HTTP Version Not Supported."
        else -> "'$this' not a known HTTP code."
    }
}
