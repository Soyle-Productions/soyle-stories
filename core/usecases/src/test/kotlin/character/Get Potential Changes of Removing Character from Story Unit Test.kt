package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.character.remove.GetPotentialChangesOfRemovingCharacterFromStory
import com.soyle.stories.usecase.character.remove.GetPotentialChangesOfRemovingCharacterFromStoryUseCase
import com.soyle.stories.usecase.character.remove.PotentialChangesOfRemovingCharacterFromStory
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.scene.character.effects.IncludedCharacterNotInProject
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Get Potential Changes of Removing Character from Story Unit Test` {

    // pre conditions
    private val character = makeCharacter()
    private val scenes = List(2) { makeScene().withCharacterIncluded(character).scene }
    private val storyEvents = scenes.map { makeStoryEvent(sceneId = it.id).withCharacterInvolved(character).storyEvent }

    // wiring
    private val characterRepository = CharacterRepositoryDouble()
    private val sceneRepository = SceneRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()

    init {
        characterRepository.givenCharacter(character)
        scenes.onEach(sceneRepository::givenScene)
        storyEvents.onEach(storyEventRepository::givenStoryEvent)
    }

    // tests

    @Nested
    inner class `Character must exist` {

        init {
            characterRepository.characters.remove(character.id)
        }

        @Test
        fun `given character does not exist - should throw error`() {
            val result = getPotentialChanges()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(CharacterDoesNotExist(character.id))
        }
    }

    @Nested
    inner class `Character must be involved in scenes` {

        @Test
        fun `given character not in any scenes or story events - should output no effects`() {
            storyEvents.map { it.withCharacterRemoved(character.id).storyEvent }
                .onEach(storyEventRepository::givenStoryEvent)
            scenes.map { it.withCharacter(character.id)!!.removed().scene }
                .onEach(sceneRepository::givenScene)

            val result = getPotentialChanges()

            result.getOrThrow().shouldBeEmpty()
        }

        @Nested
        inner class `Can be implicitly included via story events` {

            init {
                scenes.map { it.withCharacter(character.id)!!.removed().scene }
                    .onEach(sceneRepository::givenScene)
            }

            @Test
            fun `should output that scenes will be inconsistent effects`() {
                val result = getPotentialChanges()

                val effects = result.getOrThrow()
                effects.shouldHaveSize(2)
                effects.single { it.scene == scenes[0].id }.shouldBeEqualTo(
                    ImplicitCharacterRemovedFromScene(
                        scenes[0].id,
                        scenes[0].name.value,
                        character.id,
                        character.displayName.value
                    )
                )
                effects.single { it.scene == scenes[1].id }.shouldBeEqualTo(
                    ImplicitCharacterRemovedFromScene(
                        scenes[1].id,
                        scenes[1].name.value,
                        character.id,
                        character.displayName.value
                    )
                )
            }

        }

        @Nested
        inner class `Can be explicitly included` {

            init {
                storyEvents.map { it.withCharacterRemoved(character.id).storyEvent }
                    .onEach(storyEventRepository::givenStoryEvent)
            }

            @Test
            fun `should output character will be removed from scene effects`() {
                val result = getPotentialChanges()

                val effects = result.getOrThrow()
                effects.shouldHaveSize(2)
                effects.single { it.scene == scenes[0].id }.shouldBeEqualTo(
                    IncludedCharacterNotInProject(
                        scenes[0].id,
                        scenes[0].name.value,
                        character.id,
                        character.displayName.value
                    )
                )
                effects.single { it.scene == scenes[1].id }.shouldBeEqualTo(
                    IncludedCharacterNotInProject(
                        scenes[1].id,
                        scenes[1].name.value,
                        character.id,
                        character.displayName.value
                    )
                )
            }

        }

        @Test
        fun `given character is included in scene and involved in backing story event - should output inconsistent scene effect`() {
            val result = getPotentialChanges()

            val effects = result.getOrThrow()
            effects.shouldHaveSize(2)
            effects.single { it.scene == scenes[0].id }.shouldBeEqualTo(
                IncludedCharacterNotInProject(
                    scenes[0].id,
                    scenes[0].name.value,
                    character.id,
                    character.displayName.value
                )
            )
            effects.single { it.scene == scenes[1].id }.shouldBeEqualTo(
                IncludedCharacterNotInProject(
                    scenes[1].id,
                    scenes[1].name.value,
                    character.id,
                    character.displayName.value
                )
            )
        }

    }

    private fun getPotentialChanges(): Result<PotentialChangesOfRemovingCharacterFromStory> {
        val useCase: GetPotentialChangesOfRemovingCharacterFromStory =
            GetPotentialChangesOfRemovingCharacterFromStoryUseCase(
                characterRepository,
                storyEventRepository,
                sceneRepository
            )
        var result = Result.failure<PotentialChangesOfRemovingCharacterFromStory>(Error("No response received"))
        return runBlocking {
            kotlin.runCatching {
                useCase(character.id) { result = Result.success(it) }
            }
        }.mapCatching { result.getOrThrow() }
    }

}