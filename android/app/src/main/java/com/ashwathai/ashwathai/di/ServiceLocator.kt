package com.ashwathai.ashwathai.di

import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.sdk.EngineJniAdapter

object ServiceLocator {
    fun provideInferenceEngine(): InferenceEngine {
        return EngineJniAdapter()
    }
}
