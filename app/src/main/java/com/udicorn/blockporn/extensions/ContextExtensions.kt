package com.udicorn.blockporn.extensions

import android.content.Context
import android.support.annotation.DimenRes

/**
 * Returns the dimension in pixels.
 *
 * @param dimenRes the dimension resource to fetch.
 */
fun Context.dimen(@DimenRes dimenRes: Int): Int = resources.getDimensionPixelSize(dimenRes)