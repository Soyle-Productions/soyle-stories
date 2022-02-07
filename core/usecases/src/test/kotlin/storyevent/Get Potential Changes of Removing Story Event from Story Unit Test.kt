package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.remove.*
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Get Potential Changes of Removing Story Event from Story Unit Test` {

    // Summary
    /** The specified story event is removed from the project and all references made to it are invalidated */

    // Preconditions
    /** A project has been created and opened */
    private val projectId = Project.Id()

    /** the [storyEvent] must be covered by the [scene] */
    private val scene = makeScene()
    /** the [storyEvent] must include [character]s */
    private val character = makeCharacter()
    /** the [storyEvent] must exist */
    private val storyEvent = makeStoryEvent(projectId = projectId)
        /** and be covered by the [scene] */
        .coveredByScene(scene.id).storyEvent
        /** and include characters */
        .withCharacterInvolved(character).storyEvent

    // Repositories
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val sceneRepository = SceneRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()

    init {
        storyEventRepository.givenStoryEvent(storyEvent)
        sceneRepository.givenScene(scene)
        characterRepository.givenCharacter(character)
    }

    @Nested
    inner class `Story Event Must Exist` {

        init {
            storyEventRepository.storyEvents.remove(storyEvent.id)
        }

        @Test
        fun `should throw error`() {
            val result = getPotentialChanges()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(StoryEventDoesNotExist(storyEvent.id.uuid))
        }

    }

    @Nested
    inner class `Removal may have no effect` {

        @AfterEach
        fun `should output empty response`() {
            val result = getPotentialChanges()

            result.getOrThrow().shouldBeEmpty()
        }

        @Test
        fun `story event not covered by scene`() {
            storyEvent.withoutCoverage()
                .storyEvent.also(storyEventRepository::givenStoryEvent)
        }

        @Test
        fun `scene does not exist`() {
            sceneRepository.scenes.remove(scene.id)
        }

        @Test
        fun `story event does not involve characters`() {
            storyEvent.withCharacterRemoved(character.id)
                .storyEvent.also(storyEventRepository::givenStoryEvent)
        }

        @Test
        fun `scene includes character`() {
            scene.withCharacterIncluded(character)
                .scene.also(sceneRepository::givenScene)
        }

        @Test
        fun `other covered story event involves character`() {
            makeStoryEvent().coveredByScene(scene.id)
                .storyEvent.withCharacterInvolved(character)
                .storyEvent.also(storyEventRepository::givenStoryEvent)
        }

    }

    @Nested
    inner class `Involved Characters Must Exist` {

        init {
            characterRepository.characters.remove(character.id)
        }

        @Test
        fun `given involved character does not exist - should output null name`() {
            val result = getPotentialChanges()

            val effects = result.getOrThrow()
            effects.shouldHaveSize(1)
            effects.single { it.character == character.id }.let {
                it.characterName.shouldBeNull()
                it.scene.shouldBeEqualTo(scene.id)
                it.sceneName.shouldBeEqualTo(scene.name.value)
            }
        }

    }

    @Test
    fun `should output character removed from scene effect`() {
        val result = getPotentialChanges()

        val effects = result.getOrThrow()
        effects.shouldHaveSize(1)
        effects.single { it.character == character.id }.let {
            it.characterName.shouldBeEqualTo(character.displayName.value)
            it.scene.shouldBeEqualTo(scene.id)
            it.sceneName.shouldBeEqualTo(scene.name.value)
        }
    }


    private fun getPotentialChanges(): Result<PotentialChangesOfRemovingStoryEventFromProject> {
        val useCase: GetPotentialChangesOfRemovingStoryEventFromProject =
            GetPotentialChangesOfRemovingStoryEventFromProjectUseCase(storyEventRepository, sceneRepository, characterRepository)
        var result = Result.failure<PotentialChangesOfRemovingStoryEventFromProject>(Error("No response received"))
        return runCatching {
            runBlocking {
                useCase.invoke(storyEvent.id) {
                    result = Result.success(it)
                }
            }
        }.mapCatching { result.getOrThrow() }
    }
}