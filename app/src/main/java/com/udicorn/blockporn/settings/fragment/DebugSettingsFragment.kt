package com.udicorn.blockporn.settings.fragment

import com.udicorn.blockporn.BrowserApp
import com.udicorn.blockporn.R
import com.udicorn.blockporn.preference.PreferenceManager
import com.udicorn.blockporn.utils.Utils
import android.os.Bundle
import javax.inject.Inject

class DebugSettingsFragment : AbstractSettingsFragment() {

    @Inject internal lateinit var preferenceManager: PreferenceManager

    override fun providePreferencesXmlResource() = R.xml.preference_debug

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BrowserApp.appComponent.inject(this)
        addPreferencesFromResource(R.xml.preference_debug)

        togglePreference(
                preference = LEAK_CANARY,
                isChecked = preferenceManager.useLeakCanary,
                onCheckChange = {
                    activity?.let {
                        Utils.showSnackbar(it, R.string.app_restart)
                    }
                    preferenceManager.useLeakCanary = it
                }
        )
    }

    companion object {
        private const val LEAK_CANARY = "leak_canary_enabled"
    }
}
