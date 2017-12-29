package com.udicorn.blockporn.search.suggestions

import com.udicorn.blockporn.R
import com.udicorn.blockporn.constant.UTF8
import com.udicorn.blockporn.database.HistoryItem
import com.udicorn.blockporn.extensions.map
import com.udicorn.blockporn.utils.FileUtils
import android.app.Application
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

/**
 * The search suggestions provider for the DuckDuckGo search engine.
 */
class DuckSuggestionsModel(application: Application) : BaseSuggestionsModel(application, UTF8) {

    private val searchSubtitle = application.getString(R.string.suggestion)

    override fun createQueryUrl(query: String, language: String): String =
            "https://duckduckgo.com/ac/?q=$query"

    @Throws(Exception::class)
    override fun parseResults(inputStream: InputStream): List<HistoryItem> {
        val content = FileUtils.readStringFromStream(inputStream, UTF8)
        val jsonArray = JSONArray(content)

        return jsonArray
                .map { it as JSONObject }
                .map { it.getString("phrase") }
                .map { HistoryItem("$searchSubtitle \"$it\"", it, R.drawable.ic_search) }
    }

}
