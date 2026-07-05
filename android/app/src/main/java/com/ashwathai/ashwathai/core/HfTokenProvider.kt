package com.ashwathai.ashwathai.core

import android.content.Context

object HfTokenProvider {
    private var context: Context? = null

    fun init(ctx: Context) { context = ctx }

    fun getToken(): String = Prefs.getHfToken()
    fun setToken(token: String) { Prefs.setHfToken(token) }
    fun hasToken(): Boolean = getToken().isNotBlank()
}
