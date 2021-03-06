/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.udicorn.blockporn.utils

import com.udicorn.blockporn.constant.FILE
import com.udicorn.blockporn.html.bookmark.BookmarkPage
import com.udicorn.blockporn.html.download.DownloadsPage
import com.udicorn.blockporn.html.history.HistoryPage
import com.udicorn.blockporn.html.homepage.StartPage
import android.util.Patterns
import android.webkit.URLUtil
import java.util.regex.Pattern

/**
 * Utility methods for URL manipulation.
 */
object UrlUtils {

    private val ACCEPTED_URI_SCHEMA = Pattern.compile("(?i)((?:http|https|file)://|(?:inline|data|about|javascript):|(?:.*:.*@))(.*)")
    const val QUERY_PLACE_HOLDER = "%s"
    private const val URL_ENCODED_SPACE = "%20"

    /**
     * Attempts to determine whether user input is a URL or search terms.  Anything with a space is
     * passed to search if [canBeSearch] is true.
     *
     * Converts to lowercase any mistakenly upper-cased scheme (i.e., "Http://" converts to
     * "http://")
     *
     * @param canBeSearch if true, will return a search url if it isn't a valid  URL. If false,
     * invalid URLs will return null.
     * @return original or modified URL.
     */
    @JvmStatic
    fun smartUrlFilter(url: String, canBeSearch: Boolean, searchUrl: String): String {
        var inUrl = url.trim()
        val hasSpace = inUrl.contains(' ')
        val matcher = ACCEPTED_URI_SCHEMA.matcher(inUrl)
        if (matcher.matches()) {
            // force scheme to lowercase
            val scheme = matcher.group(1)
            val lcScheme = scheme.toLowerCase()
            if (lcScheme != scheme) {
                inUrl = lcScheme + matcher.group(2)
            }
            if (hasSpace && Patterns.WEB_URL.matcher(inUrl).matches()) {
                inUrl = inUrl.replace(" ", URL_ENCODED_SPACE)
            }
            return inUrl
        }
        if (!hasSpace) {
            if (Patterns.WEB_URL.matcher(inUrl).matches()) {
                return URLUtil.guessUrl(inUrl)
            }
        }

        return if (canBeSearch) {
            URLUtil.composeSearchUrl(inUrl,
                    searchUrl, QUERY_PLACE_HOLDER)
        } else {
            ""
        }
    }

    /**
     * Returns whether the given url is the bookmarks/history page or a normal website
     */
    @JvmStatic
    fun isSpecialUrl(url: String?): Boolean {
        return url != null
                && url.startsWith(FILE)
                && (url.endsWith(BookmarkPage.FILENAME)
                || url.endsWith(DownloadsPage.FILENAME)
                || url.endsWith(HistoryPage.FILENAME)
                || url.endsWith(StartPage.FILENAME))
    }

    /**
     * Determines if the url is a url for the bookmark page.
     *
     * @param url the url to check, may be null.
     * @return true if the url is a bookmark url, false otherwise.
     */
    @JvmStatic
    fun isBookmarkUrl(url: String?): Boolean =
            url != null && url.startsWith(FILE) && url.endsWith(BookmarkPage.FILENAME)

    /**
     * Determines if the url is a url for the bookmark page.
     *
     * @param url the url to check, may be null.
     * @return true if the url is a bookmark url, false otherwise.
     */
    @JvmStatic
    fun isDownloadsUrl(url: String?): Boolean =
            url != null && url.startsWith(FILE) && url.endsWith(DownloadsPage.FILENAME)

    /**
     * Determines if the url is a url for the history page.
     *
     * @param url the url to check, may be null.
     * @return true if the url is a history url, false otherwise.
     */
    @JvmStatic
    fun isHistoryUrl(url: String?): Boolean =
            url != null && url.startsWith(FILE) && url.endsWith(HistoryPage.FILENAME)

    /**
     * Determines if the url is a url for the start page.
     *
     * @param url the url to check, may be null.
     * @return true if the url is a start page url, false otherwise.
     */
    @JvmStatic
    fun isStartPageUrl(url: String?): Boolean =
            url != null && url.startsWith(FILE) && url.endsWith(StartPage.FILENAME)
}