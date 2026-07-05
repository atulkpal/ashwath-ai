package com.ashwathai.ashwathai.core

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "ashwath_prefs"
    private const val KEY_HF_TOKEN = "hf_token"
    private const val KEY_OLLAMA_HOST = "ollama_host"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun getHfToken(): String = prefs?.getString(KEY_HF_TOKEN, "") ?: ""
    fun setHfToken(token: String) { prefs?.edit()?.putString(KEY_HF_TOKEN, token)?.apply() }

    fun getOllamaHost(): String = prefs?.getString(KEY_OLLAMA_HOST, "") ?: ""
    fun setOllamaHost(host: String) { prefs?.edit()?.putString(KEY_OLLAMA_HOST, host)?.apply() }

    fun getOllamaHost(context: Context): String {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getString(KEY_OLLAMA_HOST, "") ?: ""
    }
    fun setOllamaHost(context: Context, host: String) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_OLLAMA_HOST, host).apply()
    }
}
