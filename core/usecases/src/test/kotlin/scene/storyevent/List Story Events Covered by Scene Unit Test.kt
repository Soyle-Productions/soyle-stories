package com.soyle.stories.usecase.scene.storyevent

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredBySceneUseCase
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Story Events Covered by Scene Unit Test` {

    // Summary
    /** Reports the story events that are covered by the requested scene */

    // Preconditions
    /** A project has been created and opened */
    private val projectId = Project.Id()

    /** The scene exists */
    private val scene = makeScene(projectId = projectId)

    // post conditions
    /** outputs a list of story event items */
    private var storyEventItems: StoryEventsInScene? = null

    // wiring
    private val sceneRepository = SceneRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()

    // Use Case
    private val useCase: ListStoryEventsCoveredByScene =
        ListStoryEventsCoveredBySceneUseCase(sceneRepository, storyEventRepository, characterRepository)

    private fun listStoryEventsCoveredByScene(sceneId: Scene.Id = scene.id) {
        runBlocking {
            useCase.invoke(sceneId) {
                storyEventItems = it
            }
        }
    }

    @Test
    fun `scene doesn't exist - should throw error`() {
        val error = assertThrows<SceneDoesNotExist>(::listStoryEventsCoveredByScene)
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        private val storyEvents = List(6) { makeStoryEvent(projectId = projectId) } + List(4) { makeStoryEvent() }

        init {
            storyEvents.onEach(storyEventRepository::givenStoryEvent)
        }

        @Test
        fun `no covered story events - should output empty list`() {
            listStoryEventsCoveredByScene()

            assertTrue(storyEventItems!!.isEmpty()) { "Expected to be empty" }
        }

        @Nested
        inner class `Given Some Story Events are Covered by Scene` {

            private val coveredStoryEvents = storyEvents.filter { it.projectId == projectId }.shuffled().take(3)
                .map { it.coveredByScene(scene.id).storyEvent }
                .onEach(storyEventRepository::givenStoryEvent)

            @Test
            fun `should output requested scene id`() {
                listStoryEventsCoveredByScene()

                storyEventItems!!.sceneId.mustEqual(scene.id)
            }

            @Test
            fun `should output list of covered story events`() {
                listStoryEventsCoveredByScene()

                storyEventItems!!.size.mustEqual(3) { "Size of returned list was incorrect" }
                assertEquals(
                    coveredStoryEvents.map { it.id }.toSet(),
                    storyEventItems!!.map { it.storyEventId }.toSet()
                )
                storyEventItems!!.forEach { item ->
                    val backingItem = coveredStoryEvents.single { it.id == item.storyEventId }
                    item.sceneId.mustEqual(scene.id)
                    item.storyEventName.mustEqual(backingItem.name.value)
                    item.time.mustEqual(backingItem.time.toLong())
                }
            }

            @Test
            fun `given story events do not involve characters - should output empty character items`() {
                listStoryEventsCoveredByScene()

                storyEventItems!!.forEach { item ->
                    item.involvedCharacters.shouldBeEmpty()
                }
            }

            @Nested
            inner class `Given Story Events Involve Characters` {

                private val charactersByStoryEvent = coveredStoryEvents.associate {
                    it to listOf(makeCharacter(), makeCharacter())
                }.onEach { (storyEvent, characters) ->
                    storyEvent.withCharacterInvolved(characters[0])
                        .storyEvent.withCharacterInvolved(characters[1])
                        .storyEvent.also(storyEventRepository::givenStoryEvent)
                }.mapKeys { it.key.id }

                @Test
                fun `given characters do not exist - should output empty character items`() {
                    listStoryEventsCoveredByScene()

                    storyEventItems!!.forEach { item ->
                        item.involvedCharacters.shouldBeEmpty()
                    }
                }

                @Nested
                inner class `Given characters exist` {

                    private val allCharacters = charactersByStoryEvent.values.flatten()

                    init {
                        allCharacters.forEach { characterRepository.givenCharacter(it) }
                    }

                    @Test
                    fun `should output character items`() {
                        listStoryEventsCoveredByScene()

                        storyEventItems!!.forEach { item ->
                            val coveredCharacters = charactersByStoryEvent[item.storyEventId]!!
                            item.involvedCharacters.shouldHaveSize(coveredCharacters.size)
                            item.involvedCharacters.map { it.characterId }
                                .shouldContainAll(coveredCharacters.map { it.id.uuid })
                            item.involvedCharacters.forEach { characterItem ->
                                characterItem.characterName.shouldBeEqualTo(
                                    coveredCharacters.find { it.id.uuid == characterItem.characterId }!!.displayName.value
                                )
                            }
                        }
                    }

                }

            }

        }

    }


}