package com.ashwathai.ashwathai.data.repository

import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.sdk.EngineGrpcClient
import com.ashwathai.sdk.generated.InstallRequest
import com.ashwathai.sdk.generated.RemoveRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class EngineModelRepository(
    private val grpcClient: EngineGrpcClient,
) : ModelRepository {

    override fun getRecommendedModels(): Flow<List<ModelInfo>> = flow {
        val models = withContext(Dispatchers.IO) {
            val response = grpcClient.stub.listModels(com.ashwathai.sdk.generated.Empty.getDefaultInstance())
            response.modelsList.map { it.toModelInfo() }
        }
        emit(models)
    }.flowOn(Dispatchers.IO)

    override fun getInstalledModels(): Flow<List<ModelInfo>> = flow {
        val models = withContext(Dispatchers.IO) {
            val response = grpcClient.stub.listModels(com.ashwathai.sdk.generated.Empty.getDefaultInstance())
            response.modelsList.filter { it.installed }.map { it.toModelInfo() }
        }
        emit(models)
    }.flowOn(Dispatchers.IO)

    override fun getModel(id: String): Flow<ModelInfo?> = flow {
        val models = withContext(Dispatchers.IO) {
            val response = grpcClient.stub.listModels(com.ashwathai.sdk.generated.Empty.getDefaultInstance())
            response.modelsList.firstOrNull { it.id == id }?.toModelInfo()
        }
        emit(models)
    }.flowOn(Dispatchers.IO)

    override suspend fun downloadModel(id: String) {
        withContext(Dispatchers.IO) {
            grpcClient.stub.installModel(
                InstallRequest.newBuilder().setModelId(id).build()
            )
        }
    }

    override suspend fun deleteModel(id: String) {
        withContext(Dispatchers.IO) {
            grpcClient.stub.removeModel(
                RemoveRequest.newBuilder().setModelId(id).build()
            )
        }
    }

    override fun downloadProgress(modelId: String): Flow<Float> = flow {
        var done = false
        while (!done) {
            val response = withContext(Dispatchers.IO) {
                grpcClient.stub.listModels(com.ashwathai.sdk.generated.Empty.getDefaultInstance())
            }
            val model = response.modelsList.firstOrNull { it.id == modelId }
            if (model?.installed == true) {
                emit(1f)
                done = true
            } else {
                emit(0.5f)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun com.ashwathai.sdk.generated.ModelInfo.toModelInfo(): ModelInfo = ModelInfo(
        id = this.id,
        name = this.name,
        provider = this.provider,
        description = "",
        size = formatSize(this.sizeBytes),
        parameters = this.parameters,
        tags = this.tagsList.toList(),
        isInstalled = this.installed,
    )

    private fun formatSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes.toDouble() / (1024 * 1024))} MB"
        else -> "${"%.1f".format(bytes.toDouble() / (1024 * 1024 * 1024))} GB"
    }
}
