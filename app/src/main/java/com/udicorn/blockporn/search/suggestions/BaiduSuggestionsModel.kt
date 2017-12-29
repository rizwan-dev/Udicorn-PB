package com.udicorn.blockporn.search.suggestions

import com.udicorn.blockporn.R
import com.udicorn.blockporn.constant.UTF8
import com.udicorn.blockporn.database.HistoryItem
import com.udicorn.blockporn.extensions.map
import com.udicorn.blockporn.utils.FileUtils
import android.app.Application
import org.json.JSONArray
import java.io.InputStream

/**
 * The search suggestions provider for the Baidu search engine.
 */
class BaiduSuggestionsModel(
        application: Application
) : BaseSuggestionsModel(application, UTF8) {

    private val searchSubtitle = application.getString(R.string.suggestion)
    private val inputEncoding = "GBK"

    // see http://unionsug.baidu.com/su?wd=encodeURIComponent(U)
    // see http://suggestion.baidu.com/s?wd=encodeURIComponent(U)&action=opensearch
    override fun createQueryUrl(query: String, language: String): String =
            "http://suggestion.baidu.com/s?wd=$query&action=opensearch"

    @Throws(Exception::class)
    override fun parseResults(inputStream: InputStream): List<HistoryItem> {
        val content = FileUtils.readStringFromStream(inputStream, inputEncoding)
        val responseArray = JSONArray(content)
        val jsonArray = responseArray.getJSONArray(1)

        return jsonArray
                .map { it as String }
                .map { HistoryItem("$searchSubtitle \"$it\"", it, R.drawable.ic_search) }
    }

}
