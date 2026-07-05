package com.ashwathai.ashwathai.core

import android.content.Context
import android.content.SharedPreferences

object HfTokenProvider {
    private const val PREFS_NAME = "ashwath_prefs"
    private const val KEY_HF_TOKEN = "hf_token"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getToken(): String = prefs?.getString(KEY_HF_TOKEN, "") ?: ""

    fun setToken(token: String) {
        prefs?.edit()?.putString(KEY_HF_TOKEN, token)?.apply()
    }

    fun hasToken(): Boolean = getToken().isNotBlank()
}
