package com.henrypeya.library.utils

import androidx.annotation.StringRes

/** Provides string resources by their resource ID. */
interface ResourceProvider {
    fun getString(@StringRes stringResId: Int): String
}
