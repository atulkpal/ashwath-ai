package com.ashwathai.sdk.jni

interface TokenCallback {
    fun onToken(text: String?, done: Boolean)
}
