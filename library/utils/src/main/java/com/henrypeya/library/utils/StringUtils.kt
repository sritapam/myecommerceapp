package com.henrypeya.library.utils

import java.text.Normalizer

/**
 * Normalizes a string by removing diacritical marks (accents) and converting it to lowercase.
 * This is useful for case-insensitive and accent-insensitive comparisons, such as in search functions.
 *
 * @return The normalized string.
 */
object StringUtils {

    fun normalizeAccentsAndLowercase(text: String): String {
        val normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD)
        val withoutAccents = normalizedText.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        return withoutAccents.lowercase()
    }
}
