package com.ashwathai.ashwathai.features.library

import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.ashwathai.features.library.events.LibraryEvent
import com.ashwathai.ashwathai.features.library.viewmodel.LibraryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeModelRepository(private val models: List<ModelInfo> = emptyList()) : ModelRepository {
    val downloadedIds = mutableListOf<String>()
    val deletedIds = mutableListOf<String>()

    private val _models = MutableStateFlow(models)

    override fun getRecommendedModels() = _models as MutableStateFlow<List<ModelInfo>>

    override fun getInstalledModels() = flowOf(models.filter { it.isInstalled })

    override fun getModel(id: String) = flowOf(models.find { it.id == id })

    override suspend fun downloadModel(id: String) {
        downloadedIds.add(id)
    }

    override suspend fun deleteModel(id: String) {
        deletedIds.add(id)
    }

    override fun downloadProgress(modelId: String) = flowOf(1f)
}

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {
    private lateinit var repository: FakeModelRepository
    private lateinit var viewModel: LibraryViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val installedModels = listOf(
            ModelInfo("m1", "Model One", "p", "desc", "1GB", "7B", emptyList(), true, false, 0f),
            ModelInfo("m2", "Model Two", "p", "desc", "2GB", "13B", emptyList(), true, false, 0f)
        )
        repository = FakeModelRepository(installedModels)
        viewModel = LibraryViewModel(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadInstalledModels populates installed list`() = runTest(testDispatcher) {
        viewModel.loadInstalledModels()
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals(2, state.installedModels.size)
    }

    @Test
    fun `deleteModel calls repository delete`() = runTest(testDispatcher) {
        viewModel.onEvent(LibraryEvent.DeleteModel("m1"))
        advanceUntilIdle()
        assertTrue("m1" in repository.deletedIds)
    }

    @Test
    fun `toggleModel updates activeModelId`() = runTest(testDispatcher) {
        viewModel.onEvent(LibraryEvent.ToggleModel("m1"))
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals("m1", state.activeModelId)
    }

    @Test
    fun `refresh reloads installed models`() = runTest(testDispatcher) {
        viewModel.loadInstalledModels()
        viewModel.onEvent(LibraryEvent.Refresh)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals(2, state.installedModels.size)
    }
}
