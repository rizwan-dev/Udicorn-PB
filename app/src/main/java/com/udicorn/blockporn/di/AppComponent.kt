package com.udicorn.blockporn.di

import com.udicorn.blockporn.BrowserApp
import com.udicorn.blockporn.adblock.AssetsAdBlocker
import com.udicorn.blockporn.adblock.NoOpAdBlocker
import com.udicorn.blockporn.browser.BrowserPresenter
import com.udicorn.blockporn.browser.SearchBoxModel
import com.udicorn.blockporn.browser.TabsManager
import com.udicorn.blockporn.browser.activity.BrowserActivity
import com.udicorn.blockporn.browser.activity.ThemableBrowserActivity
import com.udicorn.blockporn.browser.fragment.BookmarksFragment
import com.udicorn.blockporn.browser.fragment.TabsFragment
import com.udicorn.blockporn.dialog.LightningDialogBuilder
import com.udicorn.blockporn.download.DownloadHandler
import com.udicorn.blockporn.download.LightningDownloadListener
import com.udicorn.blockporn.html.bookmark.BookmarkPage
import com.udicorn.blockporn.html.download.DownloadsPage
import com.udicorn.blockporn.html.history.HistoryPage
import com.udicorn.blockporn.html.homepage.StartPage
import com.udicorn.blockporn.network.NetworkConnectivityModel
import com.udicorn.blockporn.reading.activity.ReadingActivity
import com.udicorn.blockporn.search.SearchEngineProvider
import com.udicorn.blockporn.search.SuggestionsAdapter
import com.udicorn.blockporn.settings.activity.ThemableSettingsActivity
import com.udicorn.blockporn.settings.fragment.*
import com.udicorn.blockporn.utils.ProxyUtils
import com.udicorn.blockporn.view.LightningChromeClient
import com.udicorn.blockporn.view.LightningView
import com.udicorn.blockporn.view.LightningWebClient
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, LightningModule::class))
interface AppComponent {

    fun inject(activity: BrowserActivity)

    fun inject(fragment: BookmarksFragment)

    fun inject(fragment: BookmarkSettingsFragment)

    fun inject(builder: LightningDialogBuilder)

    fun inject(fragment: TabsFragment)

    fun inject(lightningView: LightningView)

    fun inject(activity: ThemableBrowserActivity)

    fun inject(advancedSettingsFragment: AdvancedSettingsFragment)

    fun inject(app: BrowserApp)

    fun inject(proxyUtils: ProxyUtils)

    fun inject(activity: ReadingActivity)

    fun inject(webClient: LightningWebClient)

    fun inject(activity: ThemableSettingsActivity)

    fun inject(listener: LightningDownloadListener)

    fun inject(fragment: PrivacySettingsFragment)

    fun inject(startPage: StartPage)

    fun inject(historyPage: HistoryPage)

    fun inject(bookmarkPage: BookmarkPage)

    fun inject(downloadsPage: DownloadsPage)

    fun inject(presenter: BrowserPresenter)

    fun inject(manager: TabsManager)

    fun inject(fragment: DebugSettingsFragment)

    fun inject(suggestionsAdapter: SuggestionsAdapter)

    fun inject(chromeClient: LightningChromeClient)

    fun inject(downloadHandler: DownloadHandler)

    fun inject(searchBoxModel: SearchBoxModel)

    fun inject(searchEngineProvider: SearchEngineProvider)

    fun inject(generalSettingsFragment: GeneralSettingsFragment)

    fun inject(displaySettingsFragment: DisplaySettingsFragment)

    fun inject(networkConnectivityModel: NetworkConnectivityModel)

    fun provideAssetsAdBlocker(): AssetsAdBlocker

    fun provideNoOpAdBlocker(): NoOpAdBlocker

}
