package com.ashwathai.ashwathai.domain.repository

import com.ashwathai.ashwathai.domain.models.ModelInfo
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun getRecommendedModels(): Flow<List<ModelInfo>>
    fun getInstalledModels(): Flow<List<ModelInfo>>
    fun getModel(id: String): Flow<ModelInfo?>
    suspend fun downloadModel(id: String)
    suspend fun deleteModel(id: String)
}
