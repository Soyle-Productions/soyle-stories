package com.soyle.stories.usecase.storyevent.coverage

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyWithoutCoverage
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.coverage.uncover.GetPotentialChangesFromUncoveringStoryEvent
import com.soyle.stories.usecase.storyevent.coverage.uncover.GetPotentialChangesFromUncoveringStoryEventUseCase
import com.soyle.stories.usecase.storyevent.coverage.uncover.PotentialChangesFromUncoveringStoryEvent
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class `Get Potential Changes from Uncovering Story Event Unit Test` {

    // Pre-requisites
    /** the [storyEvent] must exist and be covered by [scene] */
    private val storyEvent = makeStoryEvent()

    /** the [scene] must exist */
    private val scene = makeScene()

    // Repositories
    private val storyEventRepository =
        StoryEventRepositoryDouble(onUpdateStoryEvent = { error("should not update scene") })
    private val sceneRepository = SceneRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()

    // Use Case
    private fun getPotentialChanges(): Result<PotentialChangesFromUncoveringStoryEvent> {
        val useCase: GetPotentialChangesFromUncoveringStoryEvent = GetPotentialChangesFromUncoveringStoryEventUseCase(
            storyEventRepository, sceneRepository, characterRepository
        )
        var result = failure<PotentialChangesFromUncoveringStoryEvent>(Error("Response not received"))
        return runCatching {
            runBlocking {
                useCase.invoke(storyEvent.id) { result = success(it) }
            }
        }.mapCatching { result.getOrThrow() }
    }

    @Nested
    inner class `Story Event Must Exist` {

        @Test
        fun `given story event doesn't exist - should throw error`() {
            val result = getPotentialChanges()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(StoryEventDoesNotExist(storyEvent.id.uuid))
        }

    }

    @Nested
    inner class `Story Event Must Be Covered by Scene` {

        init {
            storyEventRepository.givenStoryEvent(storyEvent)
        }

        @Test
        fun `given story event not covered by scene - should throw error`() {
            val result = getPotentialChanges()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(StoryEventAlreadyWithoutCoverage(storyEvent.id))
        }

    }

    @Nested
    inner class `Scene Must Exist` {

        init {
            storyEventRepository.givenStoryEvent(storyEvent.coveredByScene(Scene.Id()).storyEvent)
        }

        @Test
        fun `given scene does not exist - output should be empty`() {
            val result = getPotentialChanges()

            result.getOrThrow().shouldBeEmpty()
        }

    }

    @Nested
    inner class `Involved Characters Must Exist` {

        private val character = makeCharacter()

        init {
            sceneRepository.givenScene(scene)
            storyEvent.coveredByScene(scene.id)
                .storyEvent.withCharacterInvolved(character)
                .storyEvent.also(storyEventRepository::givenStoryEvent)
        }

        @Test
        fun `given involved character does not exist - should include in output with undefined name`() {
            val result = getPotentialChanges()

            val items = result.getOrThrow()
            items.shouldHaveSize(1)
            items.single { it.character == character.id }.let {
                it.scene.shouldBeEqualTo(scene.id)
                it.sceneName.shouldBeEqualTo(scene.name.value)
                it.characterName.shouldBeNull()
            }
        }

    }

    @Test
    fun `should output nothing`() {
        storyEventRepository.givenStoryEvent(storyEvent.coveredByScene(scene.id).storyEvent)
        sceneRepository.givenScene(scene)

        val result = getPotentialChanges()

        result.getOrThrow().shouldBeEmpty()
    }

    @Test
    fun `given story event involves characters - should output all characters`() {
        val scene = makeScene().also(sceneRepository::givenScene)
        val characters = List(2) { makeCharacter() }
            .onEach(characterRepository::givenCharacter)
        characters.fold(storyEvent) { nextStoryEvent, character -> nextStoryEvent.withCharacterInvolved(character).storyEvent }
            .coveredByScene(scene.id).storyEvent
            .also(storyEventRepository::givenStoryEvent)

        val result = getPotentialChanges()

        val items = result.getOrThrow()
        items.shouldHaveSize(2)
        items.single { it.character == characters[0].id }.let {
            it.scene.shouldBeEqualTo(scene.id)
            it.sceneName.shouldBeEqualTo(scene.name.value)
            it.characterName.shouldBeEqualTo(characters[0].displayName.value)
        }
        items.single { it.character == characters[1].id }.let {
            it.scene.shouldBeEqualTo(scene.id)
            it.sceneName.shouldBeEqualTo(scene.name.value)
            it.characterName.shouldBeEqualTo(characters[1].displayName.value)
        }
    }

    @Test
    fun `given scene includes characters - should not output explicitly included characters`() {
        val characters = List(2) { makeCharacter() }
            .onEach(characterRepository::givenCharacter)
        val scene = makeScene().withCharacterIncluded(characters[1]).scene
            .also(sceneRepository::givenScene)
        characters.fold(storyEvent) { nextStoryEvent, character -> nextStoryEvent.withCharacterInvolved(character).storyEvent }
            .coveredByScene(scene.id).storyEvent
            .also(storyEventRepository::givenStoryEvent)

        val result = getPotentialChanges()

        val items = result.getOrThrow()
        items.shouldHaveSize(1)
        items.single { it.character == characters[0].id }.let {
            it.scene.shouldBeEqualTo(scene.id)
            it.sceneName.shouldBeEqualTo(scene.name.value)
            it.characterName.shouldBeEqualTo(characters[0].displayName.value)
        }
    }

    @Test
    fun `given other covered story event involves character - should not output doubly involved characters`() {
        val characters = List(2) { makeCharacter() }
            .onEach(characterRepository::givenCharacter)
        val scene = makeScene().also(sceneRepository::givenScene)
        characters.fold(storyEvent) { nextStoryEvent, character -> nextStoryEvent.withCharacterInvolved(character).storyEvent }
            .coveredByScene(scene.id).storyEvent
            .also(storyEventRepository::givenStoryEvent)
        makeStoryEvent().coveredByScene(scene.id)
            .storyEvent.withCharacterInvolved(characters[1])
            .storyEvent.also(storyEventRepository::givenStoryEvent)

        val result = getPotentialChanges()

        val items = result.getOrThrow()
        items.shouldHaveSize(1)
        items.single { it.character == characters[0].id }.let {
            it.scene.shouldBeEqualTo(scene.id)
            it.sceneName.shouldBeEqualTo(scene.name.value)
            it.characterName.shouldBeEqualTo(characters[0].displayName.value)
        }
    }

}