/*
 * Copyright 2014 A.C.R. Development
 */
package com.udicorn.blockporn.settings.fragment

import com.udicorn.blockporn.BuildConfig
import com.udicorn.blockporn.R
import android.os.Bundle

class AboutSettingsFragment : AbstractSettingsFragment() {

    override fun providePreferencesXmlResource() = R.xml.preference_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clickablePreference(
                preference = SETTINGS_VERSION,
                summary = BuildConfig.VERSION_NAME,
                onClick = { }
        )
    }

    companion object {
        private val SETTINGS_VERSION = "pref_version"
    }
}
