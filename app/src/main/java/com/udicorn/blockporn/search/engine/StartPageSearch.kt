package com.udicorn.blockporn.search.engine

import com.udicorn.blockporn.R

/**
 * The StartPage search engine.
 */
class StartPageSearch : BaseSearchEngine(
        "file:///android_asset/startpage.png",
        "https://startpage.com/do/search?language=english&query=",
        R.string.search_engine_startpage
)
