package com.udicorn.blockporn.search.engine

import com.udicorn.blockporn.R

/**
 * The Google search engine.
 *
 * See https://www.google.com/images/srpr/logo11w.png for the icon.
 */
class GoogleSearch : BaseSearchEngine(
        "file:///android_asset/google.png",
        "https://www.google.com/search?client=lightning&ie=UTF-8&oe=UTF-8&safe=active&q=",
        R.string.search_engine_google
)
