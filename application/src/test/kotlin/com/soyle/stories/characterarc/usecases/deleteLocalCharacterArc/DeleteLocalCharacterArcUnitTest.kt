package com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc

import arrow.core.Either
import com.soyle.stories.characterarc.FailedToDemoteCharacter
import com.soyle.stories.characterarc.LocalCharacterArcException
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeleteCharacterArc
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.TestContext
import com.soyle.stories.layout.entities.*
import com.soyle.stories.mustEqual
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class DeleteLocalCharacterArcUnitTest {

    private val themeId = UUID.randomUUID()
    private val characterId = UUID.randomUUID()

    @Test
    fun `wraps delete character arc use case`() {
        given {  }.whenExecuted() then {
            baseUseCaseCalled.mustEqual(true) { "Delete Character Arc Use Case should have been called." }
            baseUseCaseCalledWith.mustEqual(themeId to characterId) { "Delete Character Arc Use Case called with ids not matching input" }
        }
    }

    @Test
    fun `base use case failure`() {
        val expectedException = Exception("I'm an exception created from a test!")
        given {
            baseUseCaseWillFailWith(expectedException)
        }.whenExecuted() then {
            output as FailedToDemoteCharacter
            output.cause.mustEqual(expectedException) { "Use case did not fail properly when base use case failed." }

            persistedLayout.mustEqual(null) { "Layout should not have been updated" }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4])
    fun `remove tools when base use case succeeds`(numberOfCharacterArcSectionsRemoved: Int) {
        given {
            baseUseCaseWillSucceed()
            numberOfCharacterArcSectionsRemoved.characterArcSectionsWillBeRemoved()
        }.whenExecuted() then {
            output as DeleteLocalCharacterArc.ResponseModel
            output.themeId.mustEqual(themeId) { "Output theme id does not match input theme id" }
            output.characterId.mustEqual(characterId) { "Output character id does not match input character id" }
            output.removedCharacterArcSections.size.mustEqual(numberOfCharacterArcSectionsRemoved) { "Number of removed character arc sections is incorrect" }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4])
    fun `remove tools associated with character arc`(numberOfAssociatedTools: Int) {
        var toolIds: List<Tool.Id> = emptyList()
        given {
            baseUseCaseWillSucceed()
            toolIds = numberOfAssociatedTools.toolsAssociatedWithCharacterArc()
        }.whenExecuted() then {
            output as DeleteLocalCharacterArc.ResponseModel
            output.removedTools.size.mustEqual(numberOfAssociatedTools) { "Number of removed tools is incorrect" }
            output.removedTools.toSet().mustEqual(toolIds.map { it.uuid }.toSet()) { "Incorrect tool ids in output" }

            persistedLayout!!.doesNotContainToolsAssociatedWithCharacterArc()
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4])
    fun `remove tools associated with removed theme`(numberOfAssociatedTools: Int) {
        var toolIds: List<Tool.Id> = emptyList()
        given {
            baseUseCaseWillSucceed()
            themeWillBeRemoved()
            toolIds = numberOfAssociatedTools.toolsAssociatedWithTheme()
        }.whenExecuted() then {
            output as DeleteLocalCharacterArc.ResponseModel
            output.removedTools.size.mustEqual(numberOfAssociatedTools) { "Number of removed tools is incorrect" }
            output.removedTools.toSet().mustEqual(toolIds.map { it.uuid }.toSet()) { "Incorrect tool ids in output" }

            persistedLayout!!.doesNotContainToolsAssociatedWithTheme()
        }
    }
    private var baseUseCaseCalledWith: Pair<UUID, UUID>? = null
    @BeforeEach
    fun clearBaseUseCaseCalledFlag() {
        baseUseCaseCalledWith = null
    }

    private var savedLayout: Layout? = null
    @BeforeEach
    fun clearSavedLayout() {
        savedLayout = null
    }

    private fun given(setup: Setup.() -> Unit): DeleteLocalCharacterArc {
        val given = Given()
        Setup(given).setup()
        val projectId = UUID.randomUUID()
        return DeleteLocalCharacterArcUseCase(projectId, object : DeleteCharacterArc {
            override suspend fun invoke(themeId: UUID, characterId: UUID, output: DemoteMajorCharacter.OutputPort) {
                baseUseCaseCalledWith = themeId to characterId
                when (val baseUseCaseResult = given.baseUseCaseResult) {
                    is Exception -> output.receiveDemoteMajorCharacterFailure(baseUseCaseResult)
                    is DemoteMajorCharacter.ResponseModel -> output.receiveDemoteMajorCharacterResponse(baseUseCaseResult)
                    else -> output.receiveDemoteMajorCharacterResponse(DemoteMajorCharacter.ResponseModel(themeId, characterId,
                        List(given.characterArcSectionsInTheme) { UUID.randomUUID() }, given.themeWillBeRemoved))
                }
            }
        }, TestContext(
            initialLayouts = listOf(
                layout(Project.Id(projectId), Layout.Id(UUID.randomUUID())) {
                    window {
                        primaryStack {
                            given.toolsAssociatedWithCharacterAndTheme.forEach {
                                this += BaseStoryStructureTool(it, Theme.Id(themeId), Character.Id(characterId), false)
                            }
                            given.toolsAssociatedWithTheme.forEach {
                                this += CharacterComparisonTool(it, Theme.Id(themeId), Character.Id(characterId), false)
                            }
                        }
                    }
                }
            ),
            saveLayout = { savedLayout = it }
        ))
    }

    private fun DeleteLocalCharacterArc.whenExecuted(): Result {
        val output = object : DeleteLocalCharacterArc.OutputPort {
            var result: Any? = null
            override fun receiveDeleteLocalCharacterArcFailure(failure: LocalCharacterArcException) {
                result = failure
            }

            override fun receiveDeleteLocalCharacterArcResponse(response: DeleteLocalCharacterArc.ResponseModel) {
                result = response
            }
        }
        return runBlocking {
            invoke(themeId, characterId, output)
            Result(baseUseCaseCalledWith, baseUseCaseCalledWith != null, output.result, savedLayout)
        }
    }

    private infix fun Result.then(assertions: Result.() -> Unit) {
        assertions()
    }

    private inner class Setup(private val given: Given) {
        fun baseUseCaseWillFailWith(e: Exception) {
            given.baseUseCaseResult = e
        }
        fun baseUseCaseWillSucceed() {
            given.baseUseCaseResult = null
        }

        fun Int.characterArcSectionsWillBeRemoved() {
            given.characterArcSectionsInTheme = this
        }

        fun Int.toolsAssociatedWithCharacterArc(): List<Tool.Id> {
            given.toolsAssociatedWithCharacterAndTheme = List(this) { Tool.Id(UUID.randomUUID()) }
            return given.toolsAssociatedWithCharacterAndTheme
        }

        fun themeWillBeRemoved() {
            given.themeWillBeRemoved = true
        }

        fun Int.toolsAssociatedWithTheme(): List<Tool.Id> {
            given.toolsAssociatedWithTheme = List(this) { Tool.Id(UUID.randomUUID()) }
            return given.toolsAssociatedWithTheme
        }

        fun Int.themesWithoutMatchingId() {
            given.themesWithoutMatchingId = this
        }
        fun themeExists() {
            given.themeWithMatchingId = (Theme.takeNoteOf("").map {
                Theme(Theme.Id(themeId), it.centralMoralQuestion, it.characters.associateBy { it.id }, it.similaritiesBetweenCharacters)
            } as Either.Right).b
        }
        fun themeDoesNotIncludeCharacter() {
        }
    }

    private inner class Given {
        var themesWithoutMatchingId: Int? = 0
        var themeWithMatchingId: Theme? = null
        var baseUseCaseResult: Any? = null
        var characterArcSectionsInTheme: Int = 0
        var toolsAssociatedWithCharacterAndTheme: List<Tool.Id> = emptyList()
        var toolsAssociatedWithTheme: List<Tool.Id> = emptyList()
        var themeWillBeRemoved: Boolean = false
    }

    private inner class Result(
        val baseUseCaseCalledWith: Pair<UUID, UUID>?,
        val baseUseCaseCalled: Boolean,
        val output: Any?,
        val persistedLayout: Layout?
    ) {

        fun Layout.doesNotContainToolsAssociatedWithCharacterArc() {
            tools.none {
                when (it) {
                    is BaseStoryStructureTool -> it.identifyingData == Theme.Id(themeId) to Character.Id(characterId)
                    else -> false
                }
            }.mustEqual(true) { "Layout still contains tools associated with character arc" }
        }

        fun Layout.doesNotContainToolsAssociatedWithTheme() {
            tools.none {
                when (it) {
                    is CharacterComparisonTool -> it.identifyingData == Theme.Id(themeId)
                    else -> false
                }
            }.mustEqual(true) { "Layout still contains tools associated with theme" }
        }

    }

}