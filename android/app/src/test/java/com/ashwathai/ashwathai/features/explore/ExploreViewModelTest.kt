package com.ashwathai.ashwathai.features.explore

import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.ashwathai.features.explore.events.ExploreEvent
import com.ashwathai.ashwathai.features.explore.viewmodel.ExploreViewModel
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
}

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {
    private lateinit var repository: FakeModelRepository
    private lateinit var viewModel: ExploreViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val sampleModels = listOf(
            ModelInfo("model-a", "Model A", "provider", "desc", "1GB", "7B", emptyList(), false, false, 0f),
            ModelInfo("model-b", "Model B", "provider", "desc", "2GB", "13B", emptyList(), false, false, 0f)
        )
        repository = FakeModelRepository(sampleModels)
        viewModel = ExploreViewModel(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadModels populates featured list`() = runTest(testDispatcher) {
        viewModel.loadModels()
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals(2, state.featuredModels.size)
    }

    @Test
    fun `downloadModel calls repository download`() = runTest(testDispatcher) {
        viewModel.onEvent(ExploreEvent.DownloadModel("model-a"))
        advanceUntilIdle()
        assertTrue("model-a" in repository.downloadedIds)
    }

    @Test
    fun `search updates searchQuery`() = runTest(testDispatcher) {
        viewModel.onEvent(ExploreEvent.Search("Model A"))
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals("Model A", state.searchQuery)
    }

    @Test
    fun `selectCategory updates category`() = runTest(testDispatcher) {
        viewModel.onEvent(ExploreEvent.SelectCategory("Coding"))
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals("Coding", state.selectedCategory)
    }

    @Test
    fun `refresh reloads models`() = runTest(testDispatcher) {
        viewModel.loadModels()
        viewModel.onEvent(ExploreEvent.Refresh)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals(2, state.featuredModels.size)
    }
}
