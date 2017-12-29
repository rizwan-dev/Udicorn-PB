package com.udicorn.blockporn.search.engine

import com.udicorn.blockporn.R

/**
 * The DuckDuckGo search engine.
 *
 * See https://duckduckgo.com/assets/logo_homepage.normal.v101.png for the icon.
 */
class DuckSearch : BaseSearchEngine(
        "file:///android_asset/duckduckgo.png",
        "https://duckduckgo.com/?t=lightning&q=",
        R.string.search_engine_duckduckgo
)
