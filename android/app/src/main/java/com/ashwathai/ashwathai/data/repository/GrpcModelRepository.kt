package com.ashwathai.ashwathai.data.repository

import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.sdk.EngineGrpcClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GrpcModelRepository(private val grpcClient: EngineGrpcClient) : ModelRepository {

    override fun getRecommendedModels(): Flow<List<ModelInfo>> = flow {
        val result = grpcClient.listModels()
        emit(result.map { list -> list.modelsList.map { it.toDomain() } }
            .getOrDefault(emptyList()))
    }

    override fun getInstalledModels(): Flow<List<ModelInfo>> = flow {
        val result = grpcClient.listModels()
        emit(result.map { list ->
            list.modelsList.filter { it.installed }.map { it.toDomain() }
        }.getOrDefault(emptyList()))
    }

    override fun getModel(id: String): Flow<ModelInfo?> = flow {
        val result = grpcClient.listModels()
        emit(result.map { list ->
            list.modelsList.firstOrNull { it.id == id }?.toDomain()
        }.getOrDefault(null))
    }

    override suspend fun downloadModel(id: String) {
        grpcClient.installModel(id)
    }

    override suspend fun deleteModel(id: String) {
        grpcClient.removeModel(id)
    }

    override fun downloadProgress(modelId: String): Flow<Float> = flow {
        while (true) {
            val result = grpcClient.listModels()
            val model = result.getOrNull()?.modelsList?.firstOrNull { it.id == modelId }
            if (model?.installed == true) {
                emit(1f)
                break
            }
            emit(0.5f)
            kotlinx.coroutines.delay(1000)
        }
    }

    private fun com.ashwathai.sdk.generated.ModelInfo.toDomain(): ModelInfo = ModelInfo(
        id = id,
        name = name,
        provider = provider,
        description = "",
        size = formatSize(sizeBytes),
        parameters = parameters,
        tags = tagsList,
        isInstalled = installed,
        isDownloading = false,
        progress = 0f
    )

    private fun formatSize(bytes: Long): String = when {
        bytes <= 0 -> "Unknown"
        bytes < 1_000_000_000 -> "${bytes / 1_000_000}MB"
        else -> String.format("%.1fGB", bytes / 1_000_000_000.0)
    }
}
