package com.soyle.stories.theme.usecases

import com.soyle.stories.common.PairOf
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.Context
import com.soyle.stories.layout.TestContext
import com.soyle.stories.layout.entities.*
import com.soyle.stories.theme.LocalThemeException
import com.soyle.stories.theme.RemoveCharacterFromComparisonFailure
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparisonUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RemoveCharacterFromLocalComparisonUnitTest {

    private val projectId = UUID.randomUUID()
    private val themeId = UUID.randomUUID()
    private val characterId = UUID.randomUUID()
    private val baseUseCaseFailure = object : ThemeException() {
        override val themeId: UUID = this@RemoveCharacterFromLocalComparisonUnitTest.themeId
    }
    private val ids = sequence {
        while (true) yield(UUID.randomUUID())
    }

    private var baseUseCaseResult: Any? = null
    private lateinit var context: Context
    private var updatedLayout: Layout? = null
    private var result: Any? = null
    private var baseUseCaseCalled: Boolean = false
    private var baseUseCaseThemeId: UUID? = null
    private var baseUseCaseCharacterId: UUID? = null
    @BeforeEach
    fun clearPrevious() {
        baseUseCaseResult = null
        context = TestContext(initialLayouts = listOf(defaultLayout(Project.Id(projectId), Layout.Id(UUID.randomUUID()))))
        updatedLayout = null
        result = null
        baseUseCaseCalled = false
        baseUseCaseThemeId = null
        baseUseCaseCharacterId = null
    }

    @Test
    fun `wraps remove character from theme use case`() {
        whenUseCaseIsExecuted()
        assertTrue(baseUseCaseCalled)
        assertEquals(themeId, baseUseCaseThemeId)
        assertEquals(characterId, baseUseCaseCharacterId)
    }

    @Test
    fun `base use case throws error`() {
        givenBaseUseCase(willFail = true)
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromComparisonFailure
        assertEquals(baseUseCaseFailure, result.cause)
    }

    @Test
    fun `base use case success`() {
        givenBaseUseCase(willFail = false)
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromLocalComparison.ResponseModel
        assertEquals(themeId, result.themeId)
        assertEquals(characterId, result.characterId)
        assertFalse(result.themeRemoved)
    }

    @Test
    fun `layout doesn't exist`() {
        givenBaseUseCase(willFail = false, willRemoveTheme = true)
        givenNoLayout()
        whenUseCaseIsExecuted()
        result as RemoveCharacterFromLocalComparison.ResponseModel
    }

    @Test
    fun `theme removed with no tools`() {
        givenBaseUseCase(willFail = false, willRemoveTheme = true)
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromLocalComparison.ResponseModel
        assertTrue(result.themeRemoved)
    }

    @Test
    fun `theme removed with base story structure tools`() {
        givenBaseUseCase(willFail = false, willRemoveTheme = true)
        givenBaseStoryStructureTools(withIds = ids.take(4), andIdentifiedWith = themeId to characterId)
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromLocalComparison.ResponseModel
        assertEquals(4, result.removedTools.size)
    }

    @Test
    fun `theme removed with character comp tools`() {
        givenBaseUseCase(willFail = false, willRemoveTheme = true)
        givenCharacterComparisonTools(withIds = ids.take(4), andIdentifiedWith = themeId)
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromLocalComparison.ResponseModel
        assertEquals(4, result.removedTools.size)
    }

    @Test
    fun `don't remove tools if theme not removed`() {
        givenBaseUseCase(willFail = false, willRemoveTheme = false)
        givenBaseStoryStructureTools(withIds = ids.take(4), andIdentifiedWith = themeId to characterId)
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromLocalComparison.ResponseModel
        assertEquals(0, result.removedTools.size)
    }

    @Test
    fun `tool removal persisted`() {
        givenBaseUseCase(willFail = false, willRemoveTheme = true)
        givenBaseStoryStructureTools(withIds = ids.take(4), andIdentifiedWith = themeId to characterId)
        whenUseCaseIsExecuted()
        val updatedLayout = updatedLayout!!
        assertLayoutHasNoToolsIdentifiedWithThemeId(updatedLayout)
    }

    private fun givenBaseUseCase(willFail: Boolean = false, willRemoveTheme: Boolean = false) {
        baseUseCaseResult = if (willFail) {
            baseUseCaseFailure
        } else {
            RemoveCharacterFromComparison.ResponseModel(themeId, characterId, willRemoveTheme)
        }
    }

    private fun givenNoLayout() {
        context = TestContext(
            initialLayouts = listOf(),
            saveLayout = {
                updatedLayout = it
            }
        )
    }

    private fun givenBaseStoryStructureTools(withIds: Sequence<UUID>, andIdentifiedWith: PairOf<UUID>) {
        context = TestContext(
            initialLayouts = listOf(
                layout(Project.Id(projectId), Layout.Id(UUID.randomUUID())) {
                    window {
                        primaryStack {
                            withIds.forEach {
                                tool(Tool.BaseStoryStructure(Tool.Id(it), Theme.Id(andIdentifiedWith.first), Character.Id(andIdentifiedWith.second), false))
                            }
                        }
                    }
                }
            ),
            saveLayout = {
                updatedLayout = it
            }
        )
    }

    private fun givenCharacterComparisonTools(withIds: Sequence<UUID>, andIdentifiedWith: UUID) {
        context = TestContext(
            initialLayouts = listOf(
                layout(Project.Id(projectId), Layout.Id(UUID.randomUUID())) {
                    window {
                        primaryStack {
                            withIds.forEach {
                                tool(Tool.CharacterComparison(Tool.Id(it), Theme.Id(andIdentifiedWith), Character.Id(characterId), false))
                            }
                        }
                    }
                }
            ),
            saveLayout = {
                updatedLayout = it
            }
        )
    }

    private fun whenUseCaseIsExecuted() {
        val baseUseCase = object : RemoveCharacterFromComparison {
            override suspend fun invoke(
                themeId: UUID,
                characterId: UUID,
                outputPort: RemoveCharacterFromComparison.OutputPort
            ) {
                baseUseCaseCalled = true
                baseUseCaseThemeId = themeId
                baseUseCaseCharacterId = characterId

                when (val baseUseCaseResult = baseUseCaseResult) {
                    is ThemeException -> outputPort.receiveRemoveCharacterFromComparisonFailure(baseUseCaseResult)
                    is RemoveCharacterFromComparison.ResponseModel -> outputPort.receiveRemoveCharacterFromComparisonResponse(baseUseCaseResult)
                    else -> RemoveCharacterFromComparison.ResponseModel(themeId, characterId, false)
                        .let(outputPort::receiveRemoveCharacterFromComparisonResponse)
                }
            }
        }
        val output = object: RemoveCharacterFromLocalComparison.OutputPort {
            override fun receiveRemoveCharacterFromLocalComparisonFailure(failure: LocalThemeException) {
                result = failure
            }

            override fun receiveRemoveCharacterFromLocalComparisonResponse(response: RemoveCharacterFromLocalComparison.ResponseModel) {
                result = response
            }
        }
        val useCase: RemoveCharacterFromLocalComparison = RemoveCharacterFromLocalComparisonUseCase(projectId, baseUseCase, context)
        runBlocking {
            useCase.invoke(themeId, characterId, output)
        }
    }

    private fun assertLayoutHasNoToolsIdentifiedWithThemeId(layout: Layout)
    {
        assertTrue(layout.tools.none {
            it.identifiedWithAnyThemeIdIn(setOf(Theme.Id(themeId)))
        })
    }
}