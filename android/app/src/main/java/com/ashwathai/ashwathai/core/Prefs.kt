package com.ashwathai.ashwathai.core

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "ashwath_prefs"
    private const val KEY_HF_TOKEN = "hf_token"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun getHfToken(): String = prefs?.getString(KEY_HF_TOKEN, "") ?: ""
    fun setHfToken(token: String) { prefs?.edit()?.putString(KEY_HF_TOKEN, token)?.apply() }
}
