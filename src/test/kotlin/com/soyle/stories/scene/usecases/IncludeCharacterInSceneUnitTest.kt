package com.soyle.stories.scene.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.nonBlankStr
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.*
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.usecases.common.IncludedCharacterInScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.AvailableCharactersToAddToScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.GetAvailableCharactersToAddToScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInSceneUseCase
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.storyevent.doubles.StoryEventRepositoryDouble
import com.soyle.stories.storyevent.makeStoryEvent
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class IncludeCharacterInSceneUnitTest {

    private val storyEventId = StoryEvent.Id().uuid
    private val characterId = Character.Id().uuid
    private val characterName = "${UUID.randomUUID()}"
    private val sceneId = Scene.Id().uuid
    private val projectId = Project.Id()

    private var savedStoryEvent: StoryEvent? = null
    private var savedScene: Scene? = null
    private var result: Any? = null

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            whenCharacterIncludedInScene()
        }

        error shouldBe sceneDoesNotExist(sceneId)
        assertNull(savedStoryEvent)
        assertNull(savedScene)
    }

    @Test
    fun `character does not exist`() {
        givenSceneExistsWithStoryEventId()

        val error = assertThrows<CharacterDoesNotExist> {
            whenCharacterIncludedInScene()
        }

        error shouldBe characterDoesNotExist(characterId)
        assertNull(savedStoryEvent)
        assertNull(savedScene)
    }

    @Test
    fun `scene already has character`() {
        givenSceneExistsWithStoryEventId()
        givenCharacterExists()
        givenSceneIncludesCharacter()

        val error = assertThrows<SceneAlreadyContainsCharacter> {
            whenCharacterIncludedInScene()
        }

        error shouldBe sceneAlreadyContainsCharacter(sceneId, characterId)
        assertNull(savedStoryEvent)
        assertNull(savedScene)
    }

    @Test
    fun `happy path`() {
        givenSceneExistsWithStoryEventId()
        givenCharacterExists()

        whenCharacterIncludedInScene()

        assertStoryEventUpdated()
        assertSceneUpdated()
        (result as IncludeCharacterInScene.ResponseModel).run {
            includedCharacterInScene shouldBe includedCharacterInScene()
            includedCharacterInStoryEvent shouldBe includedCharacterInStoryEvent()
        }
    }

    @Test
    fun `characters previously set motivations`() {
        givenSceneExistsWithStoryEventId()
        givenCharacterExists()
        givenMotivationsForCharacterPreviouslySet()
        whenCharacterIncludedInScene()

        assertStoryEventUpdated()
        assertSceneUpdated()
        (result as IncludeCharacterInScene.ResponseModel).run {
            includedCharacterInScene shouldBe includedCharacterInScene()
            includedCharacterInStoryEvent shouldBe includedCharacterInStoryEvent()
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
            includedCharacterInScene shouldBe includedCharacterInScene()
            assertNull(includedCharacterInStoryEvent)
        }
    }

    @Nested
    inner class `Get Available Characters to Add` {

        @Test
        fun `scene does not exist`() {
            val error = assertThrows<SceneDoesNotExist> {
                invoke()
            }
            error shouldBe sceneDoesNotExist(sceneId)
        }

        @Test
        fun `No Characters in Project`() {
            givenSceneExists()
            invoke()
            (result as AvailableCharactersToAddToScene).run {
                this.sceneId.mustEqual(this@IncludeCharacterInSceneUnitTest.sceneId)
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
                this.sceneId.mustEqual(this@IncludeCharacterInSceneUnitTest.sceneId)
                size.mustEqual(allCharacters.size)
                allCharacters.forEach { baseCharacter ->
                    val availableCharacter =
                        find { it.characterId == baseCharacter.id.uuid } ?: error("$baseCharacter not in output")
                    availableCharacter.characterName.mustEqual(baseCharacter.name) { "Output character name does not match expected" }
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
                this.sceneId.mustEqual(this@IncludeCharacterInSceneUnitTest.sceneId)
                size.mustEqual(allCharacters.size - includedCharacters.size)
                (allCharacters - includedCharacters).forEach { baseCharacter ->
                    val availableCharacter =
                        find { it.characterId == baseCharacter.id.uuid } ?: error("$baseCharacter not in output")
                    availableCharacter.characterName.mustEqual(baseCharacter.name) { "Output character name does not match expected" }
                    availableCharacter.mediaId.mustEqual(baseCharacter.media?.uuid) { "Output character media id does not match expected" }
                }
            }
        }

        fun invoke() {
            runBlocking {
                useCase.invoke(sceneId, output)
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
                    Scene.Id(sceneId),
                    projectId,
                    storyEventId = StoryEvent.Id(storyEventId)
                )
            ) { nextScene, character ->
                nextScene.withCharacterIncluded(character)
            }
        )
    }

    private fun givenSceneExistsWithStoryEventId() {
        givenSceneExists()
        storyEventRepository.givenStoryEvent(makeStoryEvent(StoryEvent.Id(storyEventId)))
    }

    private fun givenCharacterExists() {
        runBlocking {
            characterRepository.addNewCharacter(makeCharacter(Character.Id(characterId), projectId, characterName))
        }
    }

    private fun givenSceneIncludesCharacter() {
        sceneRepository.scenes[Scene.Id(sceneId)]!!.withCharacterIncluded(
            makeCharacter(
                Character.Id(characterId),
                Project.Id(),
                characterName
            )
        ).let {
            sceneRepository.scenes[it.id] = it
        }
    }

    private var lastMotiveSource: Scene? = null

    private fun givenMotivationsForCharacterPreviouslySet() {
        repeat(5) {
            Scene(projectId, nonBlankStr(), StoryEvent.Id())
                .withCharacterIncluded(characterRepository.characters.values.first())
                .withMotivationForCharacter(Character.Id(characterId), "${UUID.randomUUID()}")
                .let {
                    sceneRepository.scenes[it.id] = it
                }
        }
        val previousIds = sceneRepository.scenes.values.filterNot { it.id.uuid == sceneId }.map { it.id }
        sceneRepository.sceneOrder[projectId] = previousIds + Scene.Id(sceneId)
        lastMotiveSource = sceneRepository.scenes.getValue(previousIds.last())
    }

    private fun whenCharacterIncludedInScene(afterStoryEvent: Boolean = false) {
        runBlocking {
            if (! afterStoryEvent) {
                useCase.invoke(
                    sceneId, characterId,
                    output
                )
            } else {
                useCase.invoke(
                    AddCharacterToStoryEvent.ResponseModel(storyEventId, characterId),
                    output
                )
            }
        }
    }

    private val useCase = IncludeCharacterInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
    private val output = object : IncludeCharacterInScene.OutputPort, GetAvailableCharactersToAddToScene.OutputPort {

        override suspend fun receiveAvailableCharactersToAddToScene(response: AvailableCharactersToAddToScene) {
            result = response
        }

        override suspend fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
            result = response
        }
    }

    private fun Any?.shouldBe(assertion: (Any?) -> Unit) = assertion(this)

    private fun noSceneExistsWithStoryEventId(storyEventId: UUID): (Any?) -> Unit = { actual ->
        actual as NoSceneExistsWithStoryEventId
        assertEquals(storyEventId, actual.storyEventId)

    }

    private fun assertSceneUpdated() {
        val actual = savedScene as Scene
        assertEquals(sceneId, actual.id.uuid)
        assertTrue(actual.includesCharacter(Character.Id(characterId)))
    }

    private fun assertStoryEventUpdated() {
        val actual = savedStoryEvent ?: error("No Story Event was saved")
        assertEquals(storyEventId, actual.id.uuid)
        assertTrue(actual.includedCharacterIds.contains(Character.Id(characterId)))
    }

    private fun includedCharacterInScene(): (Any?) -> Unit = { actual ->

        actual as IncludedCharacterInScene
        assertEquals(sceneId, actual.sceneId)
        assertEquals(characterId, actual.characterId)
        assertEquals(characterName, actual.characterName)
        assertNull(actual.motivation)
        assertInheritedMotivation(actual)
    }

    private fun assertInheritedMotivation(actual: IncludedCharacterInScene) {
        val lastMotiveSource = lastMotiveSource ?: return
        assertNotNull(actual.inheritedMotivation)
        assertEquals(lastMotiveSource.id.uuid, actual.inheritedMotivation!!.sceneId)
        assertEquals(lastMotiveSource.name.value, actual.inheritedMotivation!!.sceneName)
        assertEquals(
            lastMotiveSource.getMotivationForCharacter(Character.Id(characterId))?.motivation,
            actual.inheritedMotivation!!.motivation
        )
    }

    private fun includedCharacterInStoryEvent(): (Any?) -> Unit = { actual ->

        actual as IncludedCharacterInStoryEvent
        assertEquals(storyEventId, actual.storyEventId)
        assertEquals(characterId, actual.characterId)
    }

}