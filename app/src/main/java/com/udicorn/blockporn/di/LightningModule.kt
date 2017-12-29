package com.udicorn.blockporn.di

import com.udicorn.blockporn.adblock.whitelist.SessionWhitelistModel
import com.udicorn.blockporn.adblock.whitelist.WhitelistModel
import com.udicorn.blockporn.database.bookmark.BookmarkDatabase
import com.udicorn.blockporn.database.bookmark.BookmarkRepository
import com.udicorn.blockporn.database.downloads.DownloadsDatabase
import com.udicorn.blockporn.database.downloads.DownloadsRepository
import com.udicorn.blockporn.database.history.HistoryDatabase
import com.udicorn.blockporn.database.history.HistoryRepository
import com.udicorn.blockporn.database.whitelist.AdBlockWhitelistDatabase
import com.udicorn.blockporn.database.whitelist.AdBlockWhitelistRepository
import com.udicorn.blockporn.ssl.SessionSslWarningPreferences
import com.udicorn.blockporn.ssl.SslWarningPreferences
import dagger.Binds
import dagger.Module

/**
 * Dependency injection module used to bind implementations to interfaces.
 */
@Module
abstract class LightningModule {

    @Binds
    abstract fun provideBookmarkModel(bookmarkDatabase: BookmarkDatabase): BookmarkRepository

    @Binds
    abstract fun provideDownloadsModel(downloadsDatabase: DownloadsDatabase): DownloadsRepository

    @Binds
    abstract fun providesHistoryModel(historyDatabase: HistoryDatabase): HistoryRepository

    @Binds
    abstract fun providesAdBlockWhitelistModel(adBlockWhitelistDatabase: AdBlockWhitelistDatabase): AdBlockWhitelistRepository

    @Binds
    abstract fun providesWhitelistModel(sessionWhitelistModel: SessionWhitelistModel): WhitelistModel

    @Binds
    abstract fun providesSslWarningPreferences(sessionSslWarningPreferences: SessionSslWarningPreferences): SslWarningPreferences

}