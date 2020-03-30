package com.soyle.stories.character.usecases

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStoryUseCase
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.TestContext
import com.soyle.stories.layout.entities.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.*

@Timeout(3)
class RemoveCharacterFromLocalStoryUnitTest {

    private val projectId: UUID = UUID.randomUUID()
    private val characterId: UUID = UUID.randomUUID()

    @Test
    fun `no tools affected`() {
        given {
            noToolsIdentifiedWithCharacter()
            noThemesWillBeRemoved()
            removeCharacterFromStoryUseCaseWillSucceed()
        }
        whenUseCaseIsExecuted()
        then {
            output as RemoveCharacterFromLocalStory.ResponseModel
            output.characterId.mustEqual(characterId) { "Output character id does not match input character id" }
            output.removedThemes.isEmpty().mustEqual(true) { "Removed themes in output should be empty" }
            output.updatedThemes.isEmpty().mustEqual(true) { "Updated themes in output should be empty" }

            output.removedTools.isEmpty().mustEqual(true) { "Removed tools in output should be empty" }

            removeCharacterFromStoryWasCalled.mustEqual(true) { "Base use case was not called" }
        }
    }

    @Test
    fun `tools with character in identifying data are removed from layout`() {
        given {
            5.toolsIdentifiedWithCharacter()
            noThemesWillBeRemoved()
            removeCharacterFromStoryUseCaseWillSucceed()
        }
        whenUseCaseIsExecuted()
        then {
            output as RemoveCharacterFromLocalStory.ResponseModel
            output.characterId.mustEqual(characterId) { "Output character id does not match input character id" }
            output.removedThemes.isEmpty().mustEqual(true) { "Removed themes in output should be empty" }
            output.updatedThemes.isEmpty().mustEqual(true) { "Updated themes in output should be empty" }

            output.removedTools.size.mustEqual(5) { "Number of removed tools in output does not match number of tools with character as identifying data" }

            assertLayoutWasPersisted()
        }
    }

    @Test
    fun outputFailureIfBaseUseCaseFails() {
        given {
            removeCharacterFromStoryUseCaseWillFailWith(Exception("Expected exception in base use case"))
        }
        whenUseCaseIsExecuted()
        then {
            output as CharacterException
        }
    }

    @Test
    fun `affected themes should be output`() {
        given {
            5.themesWillBeRemoved()
            3.themesWillBeUpdated()
        }
        whenUseCaseIsExecuted()
        then {
            output as RemoveCharacterFromLocalStory.ResponseModel
            output.removedThemes.size.mustEqual(5) { "Number of removed themes in output does not match number of themes associated with character" }
            output.updatedThemes.size.mustEqual(3) { "Number of updated themes in output does not match number of themes associated with character" }
        }
    }

    @Test
    fun `tools associated with removed themes must be removed`() {
        given {
            noToolsIdentifiedWithCharacter()
            2.themesWillBeRemoved()
            2.toolsIdentifiedWithRemovedThemes()
        }
        whenUseCaseIsExecuted()
        then {
            output as RemoveCharacterFromLocalStory.ResponseModel
            output.removedTools.size.mustEqual(2) { "Number of removed tools in output does not match number of tools with themes as identifying data" }
        }
    }


    private var useCase: PreparedUseCase? = null
    @BeforeEach
    fun resetUseCase() { useCase = null }

    private fun given(setup: Setup.() -> Unit) {
        val given = Given()
        Setup(given).setup()
        useCase = PreparedUseCase(given.context, given.baseUseCaseResult)
    }

    private var endState: RemoveCharacterFromLocalStoryAssertions? = null
    @BeforeEach
    fun resetEndState() { endState = null }

    private fun whenUseCaseIsExecuted() {
        endState = useCase!!.execute()
    }

    private fun then(assertions: RemoveCharacterFromLocalStoryAssertions.() -> Unit) {
        endState!!.assertions()
    }

    private inner class Given {
        private var themeIds: List<UUID>? = null
            get() {
                if (field == null) {
                    field = List(listOf(themesRemovedCount ?: 0, themeIdentityToolCount).max()!!) { UUID.randomUUID() }
                }
                return field
            }

        var identityToolCount: Int = 0
        var themesRemovedCount: Int? = null
            set(value) {
                field = value
                themeIds = null
            }
        var themesUpdatedCount: Int? = null
            set(value) {
                field = value
                themeIds = null
            }
        var themeIdentityToolCount: Int = 0
            set(value) {
                field = value
                themeIds = null
            }
        var baseUseCaseResult: Any? = null
            get() {
                if (field == null && themesRemovedCount == null && themesUpdatedCount == null) return null
                if (field != null) return field
                return RemoveCharacterFromStory.ResponseModel(
                    characterId, themeIds!!.subList(0, themesRemovedCount ?: 0), (0 until (themesUpdatedCount ?: 0)).map { UUID.randomUUID () }
                )
            }

        val context: TestContext
            get() = TestContext(
                initialLayouts = listOf(
                    layout(Project.Id(projectId), Layout.Id(UUID.randomUUID())) {
                        window {
                            primaryStack {
                                repeat(identityToolCount) {
                                    this += BaseStoryStructureTool(Tool.Id(UUID.randomUUID()), Theme.Id(UUID.randomUUID()), Character.Id(characterId), false)
                                }
                                themeIds!!.subList(0, themeIdentityToolCount).forEach {
                                    this += BaseStoryStructureTool(Tool.Id(UUID.randomUUID()), Theme.Id(it), Character.Id(UUID.randomUUID()), false)
                                }
                            }
                        }
                    }
                )
            )
    }

    private class Setup(
        private val given: Given
    ) {
        fun removeCharacterFromStoryUseCaseWillSucceed() {
            given.baseUseCaseResult = null
        }
        fun removeCharacterFromStoryUseCaseWillFailWith(exception: Exception) {
            given.baseUseCaseResult = exception
        }
        fun noToolsIdentifiedWithCharacter() {
            given.identityToolCount = 0
        }
        fun noThemesWillBeRemoved() {
            given.themesRemovedCount = 0
        }
        fun Int.toolsIdentifiedWithCharacter() {
            given.identityToolCount = this
        }
        fun Int.themesWillBeRemoved() {
            given.themesRemovedCount = this
        }
        fun Int.toolsIdentifiedWithRemovedThemes() {
            given.themeIdentityToolCount = this
        }
        fun Int.themesWillBeUpdated() {
            given.themesUpdatedCount = this
        }
    }

    private inner class PreparedUseCase(
        private val context: TestContext,
        private val baseUseCaseResult: Any?
    ) : RemoveCharacterFromLocalStory.OutputPort {

        private var removeCharacterFromStoryCalled = false
        private val baseUseCase = object : RemoveCharacterFromStory {
            override suspend fun invoke(characterId: UUID, output: RemoveCharacterFromStory.OutputPort) {
                removeCharacterFromStoryCalled = true
                if (baseUseCaseResult is Exception) {
                    output.receiveRemoveCharacterFromStoryFailure(baseUseCaseResult)
                } else {
                    if (baseUseCaseResult is RemoveCharacterFromStory.ResponseModel) {
                        output.receiveRemoveCharacterFromStoryResponse(baseUseCaseResult)
                    } else {
                        output.receiveRemoveCharacterFromStoryResponse(RemoveCharacterFromStory.ResponseModel(characterId, emptyList(), emptyList()))
                    }
                }
            }
        }
        private val useCase: RemoveCharacterFromLocalStory = RemoveCharacterFromLocalStoryUseCase(projectId, context, baseUseCase)
        private var result: Any? = null

        fun execute(): RemoveCharacterFromLocalStoryAssertions {
            runBlocking {
                useCase.invoke(characterId, this@PreparedUseCase)
            }
            return RemoveCharacterFromLocalStoryAssertions(result, context.persistedItems, removeCharacterFromStoryCalled)
        }

        override fun receiveRemoveCharacterFromLocalStoryResponse(response: RemoveCharacterFromLocalStory.ResponseModel) {
            result = response
        }

        override fun receiveRemoveCharacterFromLocalStoryFailure(failure: CharacterException) {
            result = failure
        }
    }

    private inner class RemoveCharacterFromLocalStoryAssertions(
        val output: Any?,
        val persistedItems: List<TestContext.PersistenceLog>,
        val removeCharacterFromStoryWasCalled: Boolean
    ) {
        fun Any?.mustEqual(expected: Any?, message: () -> String = { "" }) = assertEquals(expected, this) { message() }
        fun Any?.mustNotEqual(unexpected: Any?, message: () -> String = { "" }) = assertNotEquals(unexpected, this) { message() }
        fun assertLayoutWasPersisted() {
            val (_, persistedItem) = persistedItems.single { it.type == "saveLayout" }
            persistedItem as Layout
            persistedItem.tools.none {
                it is BaseStoryStructureTool && it.identifyingData.second.uuid == characterId
            }.mustEqual(true) { "All base story structure tools with character id should have been removed" }

        }
    }
}