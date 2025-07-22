package com.henrypeya.feature_auth.ui.components

import javax.inject.Inject

interface EmailValidator {
    fun isValid(email: String): Boolean
}

class DefaultEmailValidator @Inject constructor() : EmailValidator {
    override fun isValid(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
