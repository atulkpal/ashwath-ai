package com.ashwathai.ashwathai.core

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.sdk.EmbeddedInferenceEngine

class EngineLifecycleObserver(
    private val engine: InferenceEngine,
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        Log.d("EngineLifecycle", "App foregrounded — connecting to engine")
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d("EngineLifecycle", "App backgrounded — releasing engine resources")
        if (engine is EmbeddedInferenceEngine) {
            // engine will be kept alive but backgrounded
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("EngineLifecycle", "App destroyed — stopping engine")
    }
}
