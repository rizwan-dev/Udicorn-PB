package com.udicorn.blockporn.settings.activity

import com.udicorn.blockporn.BrowserApp
import com.udicorn.blockporn.R
import com.udicorn.blockporn.preference.PreferenceManager
import com.udicorn.blockporn.utils.ThemeUtils
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import javax.inject.Inject

abstract class ThemableSettingsActivity : AppCompatPreferenceActivity() {

    private var themeId: Int = 0

    @Inject internal lateinit var preferences: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        BrowserApp.appComponent.inject(this)
        themeId = preferences.useTheme

        // set the theme
        when (themeId) {
            0 -> {
                setTheme(R.style.Theme_SettingsTheme)
                this.window.setBackgroundDrawable(ColorDrawable(ThemeUtils.getPrimaryColor(this)))
            }
            1 -> {
                setTheme(R.style.Theme_SettingsTheme_Dark)
                this.window.setBackgroundDrawable(ColorDrawable(ThemeUtils.getPrimaryColorDark(this)))
            }
            2 -> {
                setTheme(R.style.Theme_SettingsTheme_Black)
                this.window.setBackgroundDrawable(ColorDrawable(ThemeUtils.getPrimaryColorDark(this)))
            }
        }
        super.onCreate(savedInstanceState)

        resetPreferences()
    }

    private fun resetPreferences() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (preferences.useBlackStatusBar) {
                window.statusBarColor = Color.BLACK
            } else {
                window.statusBarColor = ThemeUtils.getStatusBarColor(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resetPreferences()
        if (preferences.useTheme != themeId) {
            recreate()
        }
    }

}
