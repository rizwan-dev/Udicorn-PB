package com.udicorn.blockporn.search

import com.udicorn.blockporn.BrowserApp
import com.udicorn.blockporn.R
import com.udicorn.blockporn.database.HistoryItem
import com.udicorn.blockporn.database.bookmark.BookmarkRepository
import com.udicorn.blockporn.database.history.HistoryRepository
import com.udicorn.blockporn.preference.PreferenceManager
import com.udicorn.blockporn.search.suggestions.*
import com.udicorn.blockporn.utils.ThemeUtils
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.anthonycr.bonsai.*
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.FilenameFilter
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class SuggestionsAdapter(
        private val context: Context,
        dark: Boolean,
        incognito: Boolean
) : BaseAdapter(), Filterable {

    private val filterScheduler = Schedulers.newSingleThreadedScheduler()
    private val maxSuggestions = 5

    private val filteredList = ArrayList<HistoryItem>(5)

    private val history = ArrayList<HistoryItem>(5)
    private val bookmarks = ArrayList<HistoryItem>(5)
    private val suggestions = ArrayList<HistoryItem>(5)

    private val searchDrawable: Drawable
    private val historyDrawable: Drawable
    private val bookmarkDrawable: Drawable

    private val filterComparator = SuggestionsComparator()

    @Inject internal lateinit var bookmarkManager: BookmarkRepository
    @Inject internal lateinit var preferenceManager: PreferenceManager
    @Inject internal lateinit var historyModel: HistoryRepository
    @Inject internal lateinit var application: Application
    @Inject @field:Named("database") internal lateinit var databaseScheduler: Scheduler
    @Inject @field:Named("network") internal lateinit var networkScheduler: Scheduler

    private val allBookmarks = ArrayList<HistoryItem>(5)

    private val darkTheme: Boolean
    private var isIncognito = true

    private val searchFilter: SearchFilter

    init {
        BrowserApp.appComponent.inject(this)
        darkTheme = dark || incognito
        isIncognito = incognito

        val suggestionsRepository = if (isIncognito) {
            NoOpSuggestionsRepository()
        } else {
            suggestionsRepositoryForPreference()
        }

        searchFilter = SearchFilter(suggestionsRepository,
                this,
                historyModel,
                databaseScheduler,
                networkScheduler)

        refreshBookmarks()

        searchDrawable = ThemeUtils.getThemedDrawable(context, R.drawable.ic_search, darkTheme)
        bookmarkDrawable = ThemeUtils.getThemedDrawable(context, R.drawable.ic_bookmark, darkTheme)
        historyDrawable = ThemeUtils.getThemedDrawable(context, R.drawable.ic_history, darkTheme)
    }

    private fun suggestionsRepositoryForPreference(): SuggestionsRepository =
            when (preferenceManager.searchSuggestionChoice) {
                PreferenceManager.Suggestion.SUGGESTION_GOOGLE ->
                    GoogleSuggestionsModel(application)
                PreferenceManager.Suggestion.SUGGESTION_DUCK ->
                    DuckSuggestionsModel(application)
                PreferenceManager.Suggestion.SUGGESTION_BAIDU ->
                    BaiduSuggestionsModel(application)
                PreferenceManager.Suggestion.SUGGESTION_NONE ->
                    NoOpSuggestionsRepository()
            }

    fun refreshPreferences() {
        searchFilter.suggestionsRepository = if (isIncognito) {
            NoOpSuggestionsRepository()
        } else {
            suggestionsRepositoryForPreference()
        }
    }

    // We don't need these cache files anymore
    fun clearCache() = Schedulers.io().execute(ClearCacheRunnable(application))

    fun refreshBookmarks() {
        bookmarkManager.getAllBookmarks()
                .subscribeOn(databaseScheduler)
                .subscribe { list ->
                    allBookmarks.clear()
                    allBookmarks.addAll(list)
                }
    }

    override fun getCount(): Int = filteredList.size

    override fun getItem(position: Int): Any? {
        if (position > filteredList.size || position < 0) {
            return null
        }
        return filteredList[position]
    }

    override fun getItemId(position: Int): Long = 0

    private class SuggestionHolder internal constructor(view: View) {

        internal val mImage = view.findViewById<ImageView>(R.id.suggestionIcon)
        internal val mTitle = view.findViewById<TextView>(R.id.title)
        internal val mUrl = view.findViewById<TextView>(R.id.url)

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val holder: SuggestionHolder
        val finalView: View

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            finalView = inflater.inflate(R.layout.two_line_autocomplete, parent, false)

            holder = SuggestionHolder(finalView)
            finalView.tag = holder
        } else {
            finalView = convertView
            holder = convertView.tag as SuggestionHolder
        }
        val web: HistoryItem = filteredList[position]

        holder.mTitle.text = web.title
        holder.mUrl.text = web.url

        if (darkTheme) {
            holder.mTitle.setTextColor(Color.WHITE)
        }

        val image = when (web.imageId) {
            R.drawable.ic_bookmark -> bookmarkDrawable
            R.drawable.ic_search -> searchDrawable
            R.drawable.ic_history -> historyDrawable
            else -> searchDrawable
        }

        holder.mImage.setImageDrawable(image)

        return finalView
    }

    override fun getFilter(): Filter = searchFilter

    private fun publishResults(list: List<HistoryItem>) {
        if (list != filteredList) {
            filteredList.clear()
            filteredList.addAll(list)
            notifyDataSetChanged()
        }
    }

    private fun clearSuggestions() {
        Completable.create({ subscriber ->
            bookmarks.clear()
            history.clear()
            suggestions.clear()
            subscriber.onComplete()
        }).subscribeOn(filterScheduler)
                .observeOn(Schedulers.main())
                .subscribe()
    }

    private fun combineResults(bookmarkList: List<HistoryItem>?,
                               historyList: List<HistoryItem>?,
                               suggestionList: List<HistoryItem>?) {
        Single.create(SingleAction<List<HistoryItem>> { subscriber ->
            val list = ArrayList<HistoryItem>(5)
            if (bookmarkList != null) {
                bookmarks.clear()
                bookmarks.addAll(bookmarkList)
            }
            if (historyList != null) {
                history.clear()
                history.addAll(historyList)
            }
            if (suggestionList != null) {
                suggestions.clear()
                suggestions.addAll(suggestionList)
            }
            val bookmark = bookmarks.iterator()
            val history = history.iterator()
            val suggestion = suggestions.listIterator()
            while (list.size < maxSuggestions) {
                if (!bookmark.hasNext() && !suggestion.hasNext() && !history.hasNext()) {
                    break
                }
                if (bookmark.hasNext()) {
                    list.add(bookmark.next())
                }
                if (suggestion.hasNext() && list.size < maxSuggestions) {
                    list.add(suggestion.next())
                }
                if (history.hasNext() && list.size < maxSuggestions) {
                    list.add(history.next())
                }
            }

            Collections.sort(list, filterComparator)
            subscriber.onItem(list)
            subscriber.onComplete()
        }).subscribeOn(filterScheduler)
                .observeOn(Schedulers.main())
                .subscribe(object : SingleOnSubscribe<List<HistoryItem>>() {
                    override fun onItem(item: List<HistoryItem>?) =
                            publishResults(requireNotNull(item))
                })
    }

    private fun getBookmarksForQuery(query: String): Single<List<HistoryItem>> =
            Single.create({ subscriber ->
                val bookmarks = ArrayList<HistoryItem>(5)
                var counter = 0
                for (n in allBookmarks.indices) {
                    if (counter >= 5) {
                        break
                    }
                    if (allBookmarks[n].title.toLowerCase(Locale.getDefault())
                            .startsWith(query)) {
                        bookmarks.add(allBookmarks[n])
                        counter++
                    } else if (allBookmarks[n].url.contains(query)) {
                        bookmarks.add(allBookmarks[n])
                        counter++
                    }
                }
                subscriber.onItem(bookmarks)
                subscriber.onComplete()
            })

    private class SearchFilter internal constructor(
            var suggestionsRepository: SuggestionsRepository,
            private val suggestionsAdapter: SuggestionsAdapter,
            private val historyModel: HistoryRepository,
            private val databaseScheduler: Scheduler,
            private val networkScheduler: Scheduler
    ) : Filter() {

        private var networkDisposable: Disposable? = null
        private var historyDisposable: Disposable? = null

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            if (constraint == null || constraint.isEmpty()) {
                suggestionsAdapter.clearSuggestions()
                return results
            }
            val query = constraint.toString().toLowerCase(Locale.getDefault()).trim()

            if (networkDisposable?.isDisposed != false) {
                networkDisposable = suggestionsRepository.resultsForSearch(query)
                        .subscribeOn(networkScheduler)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { item ->
                            suggestionsAdapter.combineResults(null, null, item)
                        }
            }

            suggestionsAdapter.getBookmarksForQuery(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.main())
                    .subscribe(object : SingleOnSubscribe<List<HistoryItem>>() {
                        override fun onItem(item: List<HistoryItem>?) =
                                suggestionsAdapter.combineResults(item, null, null)
                    })

            if (historyDisposable?.isDisposed != false) {
                historyDisposable = historyModel.findHistoryItemsContaining(query)
                        .subscribeOn(databaseScheduler)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { list ->
                            suggestionsAdapter.combineResults(null, list, null)
                        }
            }

            results.count = 1
            return results
        }

        override fun convertResultToString(resultValue: Any) = (resultValue as HistoryItem).url

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) =
                suggestionsAdapter.combineResults(null, null, null)
    }

    private class ClearCacheRunnable internal constructor(private val app: Application) : Runnable {

        override fun run() {
            val dir = File(app.cacheDir.toString())
            val fileList = dir.list(NameFilter())
            fileList.map { File(dir.path + it) }
                    .forEach { it.delete() }
        }

        private class NameFilter : FilenameFilter {

            private val cacheFileType = ".sgg"

            override fun accept(dir: File, filename: String) = filename.endsWith(cacheFileType)
        }
    }

    private class SuggestionsComparator : Comparator<HistoryItem> {

        override fun compare(lhs: HistoryItem, rhs: HistoryItem): Int {
            if (lhs.imageId == rhs.imageId) return 0
            if (lhs.imageId == R.drawable.ic_bookmark) return -1
            if (rhs.imageId == R.drawable.ic_bookmark) return 1
            if (lhs.imageId == R.drawable.ic_history) return -1
            return 1
        }
    }

}
