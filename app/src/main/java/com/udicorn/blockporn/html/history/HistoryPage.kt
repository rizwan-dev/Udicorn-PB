/*
 * Copyright 2014 A.C.R. Development
 */
package com.udicorn.blockporn.html.history

import com.udicorn.blockporn.BrowserApp
import com.udicorn.blockporn.constant.FILE
import com.udicorn.blockporn.database.history.HistoryRepository
import android.app.Application
import com.anthonycr.bonsai.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

class HistoryPage {

    @Inject internal lateinit var app: Application
    @Inject internal lateinit var historyModel: HistoryRepository

    init {
        BrowserApp.appComponent.inject(this)
    }

    fun createHistoryPage(): Single<String> = historyModel
            .lastHundredVisitedHistoryItems()
            .map { list ->
                val newList = list.toMutableList()

                val historyPageBuilder = HistoryPageBuilder(app)

                val historyWebPage = getHistoryPageFile(app)

                FileWriter(historyWebPage, false).use {
                    it.write(historyPageBuilder.buildPage(newList))
                }

                return@map "$FILE$historyWebPage"
            }

    companion object {

        const val FILENAME = "history.html"

        /**
         * Get the file that the history page is stored in
         * or should be stored in.
         *
         * @param application the application used to access the file.
         * @return a valid file object, note that the file might not exist.
         */
        private fun getHistoryPageFile(application: Application): File =
                File(application.filesDir, FILENAME)

        /**
         * Use this observable to immediately delete the history
         * page. This will clear the cached history page that was
         * stored on file.
         *
         * @return a completable that deletes the history page
         * when subscribed.
         */
        @JvmStatic
        fun deleteHistoryPage(application: Application): Completable =
                Completable.create { subscriber ->
                    val historyWebPage = getHistoryPageFile(application)
                    if (historyWebPage.exists()) {
                        historyWebPage.delete()
                    }

                    subscriber.onComplete()
                }
    }

}