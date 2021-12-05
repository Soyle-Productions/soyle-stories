package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.str
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.exceptions.scene.assertThrowsSceneDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.NoSceneExistsWithStoryEventId
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.scene.character.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.usecase.scene.character.includeCharacterInScene.IncludeCharacterInSceneUseCase
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import com.soyle.stories.usecase.storyevent.characterDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class IncludeCharacterInSceneUnitTest {

    private val storyEvent = makeStoryEvent()
    private val scene = makeScene(coveredStoryEvents = setOf(storyEvent.id))
    private val character = makeCharacter(projectId = scene.projectId)

    private val storyEventId = storyEvent.id
    private val characterId = character.id
    private val characterName = character.name
    private val sceneId = scene.id
    private val projectId = scene.projectId

    private var savedStoryEvent: StoryEvent? = null
    private var savedScene: Scene? = null
    private var result: Any? = null

    @Test
    fun `scene does not exist`() {
        assertThrowsSceneDoesNotExist(sceneId) {
            whenCharacterIncludedInScene()
        }

        assertNull(savedStoryEvent)
        assertNull(savedScene)
    }

    @Test
    fun `character does not exist`() {
        sceneRepository.givenScene(scene)
        storyEventRepository.givenStoryEvent(storyEvent)

        val error = assertThrows<CharacterDoesNotExist> {
            whenCharacterIncludedInScene()
        }

        error shouldBe characterDoesNotExist(characterId.uuid)
        assertNull(savedStoryEvent)
        assertNull(savedScene)
    }

    @Test
    fun `scene already has character`() {
        characterRepository.givenCharacter(character)
        sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)
        storyEventRepository.givenStoryEvent(storyEvent)

        whenCharacterIncludedInScene()

        assertNull(savedStoryEvent)
        assertNull(savedScene)
    }

    @Nested
    inner class `Given Character, Scene, and Story Event Exist`
    {
        init {
            characterRepository.givenCharacter(character)
            sceneRepository.givenScene(scene)
            storyEventRepository.givenStoryEvent(storyEvent)
        }

        @Test
        fun `scene should include character`() {
            whenCharacterIncludedInScene()

            savedScene.mustEqual(scene.withCharacterIncluded(character).scene)
        }

        @Test
        fun `story event should include character`() {
            whenCharacterIncludedInScene()

            savedScene.mustEqual(scene.withCharacterIncluded(character).scene)
        }

        @Test
        fun `should output character included in scene event`() {
            whenCharacterIncludedInScene()

            (result as IncludeCharacterInScene.ResponseModel).includedCharacterInScene.run {
                sceneId.mustEqual(scene.id)
                characterId.mustEqual(character.id)
                characterName.mustEqual(character.name)
                assertNull(motivation)
                coveredArcSections.isEmpty().mustEqual(true)
                assertNull(inheritedMotivation)
            }
        }

        @Test
        fun `should output character included in story event event`() {
            whenCharacterIncludedInScene()

            (result as IncludeCharacterInScene.ResponseModel).includedCharacterInStoryEvent.run {
                characterId.mustEqual(character.id)
                characterName.mustEqual(character.name)
            }
        }

        @Nested
        inner class `Given Previous Scene Includes Character`
        {

            private val previousMotivation = "Previous Motivation ${str()}"
            private val previousScene = makeScene(projectId = scene.projectId)
                .withCharacterIncluded(character).scene
                .withMotivationForCharacter(character.id, previousMotivation)

            init {
                sceneRepository.givenScene(previousScene)
                sceneRepository.sceneOrders[scene.projectId] = listOf(previousScene.id, scene.id)
                    .let { SceneOrder.reInstantiate(scene.projectId, it) }
            }

            @Test
            fun `included character event should have inherited motivation`() {
                whenCharacterIncludedInScene()

                (result as IncludeCharacterInScene.ResponseModel).includedCharacterInScene.run {
                    inheritedMotivation!!.sceneId.mustEqual(previousScene.id.uuid)
                    inheritedMotivation!!.sceneName.mustEqual(previousScene.name.value)
                    inheritedMotivation!!.motivation.mustEqual(previousMotivation)
                }
            }

        }

    }

    @Test
    fun `In response to story event adding character`() {
        givenSceneExistsWithStoryEventId()
        givenCharacterExists()
        whenCharacterIncludedInScene(afterStoryEvent = true)

        assertNull(savedStoryEvent)
        assertSceneUpdated()
        (result as IncludeCharacterInScene.ResponseModel).run {
            sceneId.mustEqual(this@IncludeCharacterInSceneUnitTest.sceneId)
            includedCharacterInScene shouldBe includedCharacterInScene()
            assertNull(includedCharacterInStoryEvent)
        }
    }

    @Nested
    inner class `Get Available Characters to Add` {

        @Test
        fun `scene does not exist`() {
            assertThrowsSceneDoesNotExist(sceneId) {
                invoke()
            }
        }

        @Test
        fun `No Characters in Project`() {
            givenSceneExists()
            invoke()
            (result as AvailableCharactersToAddToScene).run {
                this.sceneId.mustEqual(this@IncludeCharacterInSceneUnitTest.sceneId.uuid)
                assertTrue(isEmpty())
            }
        }

        @Test
        fun `No Characters in Scene`() {
            givenSceneExists()
            val allCharacters = List(10) { makeCharacter(projectId = projectId) }
            allCharacters.forEach(characterRepository::givenCharacter)
            invoke()
            (result as AvailableCharactersToAddToScene).run {
                this.sceneId.mustEqual(this@IncludeCharacterInSceneUnitTest.sceneId.uuid)
                size.mustEqual(allCharacters.size)
                allCharacters.forEach { baseCharacter ->
                    val availableCharacter =
                        find { it.characterId == baseCharacter.id.uuid } ?: error("$baseCharacter not in output")
                    availableCharacter.characterName.mustEqual(baseCharacter.name.value) { "Output character name does not match expected" }
                    availableCharacter.mediaId.mustEqual(baseCharacter.media?.uuid) { "Output character media id does not match expected" }
                }
            }
        }

        @Test
        fun `Some characters in Scene`() {
            val allCharacters = List(10) { makeCharacter(projectId = projectId) }
            allCharacters.forEach(characterRepository::givenCharacter)
            val includedCharacters = allCharacters.shuffled().take(4)
            givenSceneExists(includingCharacters = includedCharacters)

            invoke()

            (result as AvailableCharactersToAddToScene).run {
                this.sceneId.mustEqual(this@IncludeCharacterInSceneUnitTest.sceneId.uuid)
                size.mustEqual(allCharacters.size - includedCharacters.size)
                (allCharacters - includedCharacters).forEach { baseCharacter ->
                    val availableCharacter =
                        find { it.characterId == baseCharacter.id.uuid } ?: error("$baseCharacter not in output")
                    availableCharacter.characterName.mustEqual(baseCharacter.name.value) { "Output character name does not match expected" }
                    availableCharacter.mediaId.mustEqual(baseCharacter.media?.uuid) { "Output character media id does not match expected" }
                }
            }
        }

        fun invoke() {
            runBlocking {
                useCase.invoke(sceneId.uuid, output)
            }
        }

    }

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = {
        savedScene = it
    })
    private val storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = ::savedStoryEvent::set)
    private val characterRepository = CharacterRepositoryDouble()

    private fun givenSceneExists(includingCharacters: List<Character> = listOf()) {
        sceneRepository.givenScene(
            includingCharacters.fold(
                makeScene(
                    sceneId,
                    projectId,
                    coveredStoryEvents = setOf(storyEventId)
                )
            ) { nextScene, character ->
                nextScene.withCharacterIncluded(character).scene
            }
        )
    }

    private fun givenSceneExistsWithStoryEventId() {
        givenSceneExists()
        storyEventRepository.givenStoryEvent(makeStoryEvent(storyEventId))
    }

    private fun givenCharacterExists() {
        runBlocking {
            characterRepository.addNewCharacter(makeCharacter(characterId, projectId, characterName))
        }
    }

    private fun givenSceneIncludesCharacter() {
        sceneRepository.scenes[sceneId]!!.withCharacterIncluded(
            makeCharacter(
                characterId,
                Project.Id(),
                characterName
            )
        ).scene.let {
            sceneRepository.scenes[it.id] = it
        }
    }

    private var lastMotiveSource: Scene? = null

    private fun givenMotivationsForCharacterPreviouslySet() {
        repeat(5) {
            makeScene(projectId = projectId)
                .withCharacterIncluded(characterRepository.characters.values.first()).scene
                .withMotivationForCharacter(characterId, "${UUID.randomUUID()}")
                .let {
                    sceneRepository.scenes[it.id] = it
                }
        }
        val previousIds = sceneRepository.scenes.values.filterNot { it.id == sceneId }.map { it.id }
        sceneRepository.sceneOrders[projectId] = (previousIds + sceneId)
            .let { SceneOrder.reInstantiate(projectId, it) }
        lastMotiveSource = sceneRepository.scenes.getValue(previousIds.last())
    }

    private fun whenCharacterIncludedInScene(afterStoryEvent: Boolean = false) {
        runBlocking {
            if (! afterStoryEvent) {
                useCase.invoke(
                    sceneId.uuid, characterId.uuid,
                    output
                )
            } else {
                useCase.invoke(
                    AddCharacterToStoryEvent.ResponseModel(storyEventId.uuid, characterId.uuid),
                    output
                )
            }
        }
    }

    private val useCase = IncludeCharacterInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
    private val output = object : IncludeCharacterInScene.OutputPort, ListAvailableCharactersToIncludeInScene.OutputPort {

        override suspend fun receiveAvailableCharactersToAddToScene(response: AvailableCharactersToAddToScene) {
            result = response
        }

        override suspend fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
            result = response
        }
    }

    private infix fun Any?.shouldBe(assertion: (Any?) -> Unit) = assertion(this)

    private fun noSceneExistsWithStoryEventId(storyEventId: UUID): (Any?) -> Unit = { actual ->
        actual as NoSceneExistsWithStoryEventId
        assertEquals(storyEventId, actual.storyEventId)

    }

    private fun assertSceneUpdated() {
        val actual = savedScene as Scene
        assertEquals(sceneId, actual.id)
        assertTrue(actual.includesCharacter(characterId))
    }

    private fun assertStoryEventUpdated() {
        val actual = savedStoryEvent ?: error("No Story Event was saved")
        assertEquals(storyEventId, actual.id)
        assertTrue(actual.involvedCharacters.contains(characterId))
    }

    private fun includedCharacterInScene(): (Any?) -> Unit = { actual ->

        actual as IncludedCharacterInScene
        assertEquals(characterId, actual.characterId)
        assertEquals(characterName.value, actual.characterName)
        assertNull(actual.motivation)
        assertInheritedMotivation(actual)
    }

    private fun assertInheritedMotivation(actual: IncludedCharacterInScene) {
        val lastMotiveSource = lastMotiveSource ?: return
        assertNotNull(actual.inheritedMotivation)
        assertEquals(lastMotiveSource.id.uuid, actual.inheritedMotivation!!.sceneId)
        assertEquals(lastMotiveSource.name.value, actual.inheritedMotivation!!.sceneName)
        assertEquals(
            lastMotiveSource.getMotivationForCharacter(characterId)?.motivation,
            actual.inheritedMotivation!!.motivation
        )
    }

    private fun includedCharacterInStoryEvent(): (Any?) -> Unit = { actual ->

        actual as IncludedCharacterInStoryEvent
        assertEquals(storyEventId.uuid, actual.storyEventId)
        assertEquals(characterId.uuid, actual.characterId)
    }

}