package com.ashwathai.ashwathai

import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.ashwathai.features.explore.events.ExploreEvent
import com.ashwathai.ashwathai.features.explore.viewmodel.ExploreViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val mockModels = listOf(
        ModelInfo(id = "test-1", name = "Test Model 1", provider = "Test", description = "A test", size = "1 GB", parameters = "1B", tags = listOf("test")),
        ModelInfo(id = "test-2", name = "Test Model 2", provider = "Test", description = "B test", size = "2 GB", parameters = "2B", tags = listOf("test")),
    )

    private val mockRepo = object : ModelRepository {
        override fun getRecommendedModels() = flowOf(mockModels)
        override fun getInstalledModels() = flowOf(mockModels.take(1))
        override fun getModel(id: String) = flowOf(mockModels.find { it.id == id })
        override suspend fun downloadModel(id: String) {}
        override suspend fun deleteModel(id: String) {}
        override fun downloadProgress(modelId: String) = flowOf(1f)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load models on init`() = runTest(testDispatcher) {
        val vm = ExploreViewModel(mockRepo, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = vm.state.value
        assert(state.featuredModels.size == 2)
        assert(!state.isLoading)
    }

    @Test
    fun `search filters models`() = runTest(testDispatcher) {
        val vm = ExploreViewModel(mockRepo, testDispatcher)
        vm.onEvent(ExploreEvent.Search("Test Model 2"))
        assert(vm.state.value.searchQuery == "Test Model 2")
    }

    @Test
    fun `select category updates state`() = runTest(testDispatcher) {
        val vm = ExploreViewModel(mockRepo, testDispatcher)
        vm.onEvent(ExploreEvent.SelectCategory("General"))
        assert(vm.state.value.selectedCategory == "General")
    }
}
