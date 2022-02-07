package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Outputs the characters that have not yet been involved in any covered story events not included in the scene
 */
class `List Available Characters to Include in Scene Unit Test` {

    // Pre-Conditions

    /** The [scene] must exist */
    private val scene = makeScene()

    // Wiring

    private val sceneRepository = SceneRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()

    // Tests

    @Nested
    inner class `Scene Must Exist` {

        @Test
        fun `given scene does not exist - should return error`() {
            val result = listAvailableCharacters()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
        }

    }

    @Test
    fun `given no characters in project - should output empty response`() {
        sceneRepository.givenScene(scene)
        repeat(5) { characterRepository.givenCharacter(makeCharacter()) } // characters in different projects

        val response = listAvailableCharacters().getOrThrow()

        response.sceneId.shouldBeEqualTo(scene.id)
        response.shouldBeEmpty()
    }

    @Test
    fun `given characters in project - should output characters`() {
        sceneRepository.givenScene(scene)
        val characters = List(5) { makeCharacter(projectId = scene.projectId) }
            .onEach(characterRepository::givenCharacter)

        val response = listAvailableCharacters().getOrThrow()

        response.sceneId.shouldBeEqualTo(scene.id)
        response.shouldHaveSize(5)
        response.map { it.characterId }.shouldContainSame(characters.map { it.id.uuid })
        response.forEach { item ->
            item.characterName.shouldBeEqualTo(characters.single { it.id.uuid == item.characterId }.displayName.value)
        }
    }

    @Test
    fun `given scene includes characters - should not output included characters`() {
        val characters = List(5) { makeCharacter(projectId = scene.projectId) }
            .onEach(characterRepository::givenCharacter)
        sceneRepository.givenScene(scene.withCharacterIncluded(characters[2]).scene)

        val response = listAvailableCharacters().getOrThrow()

        response.sceneId.shouldBeEqualTo(scene.id)
        response.shouldHaveSize(4)
        response.map { it.characterId }.shouldContainSame((characters - characters[2]).map { it.id.uuid })
    }

    @Test
    fun `given covered story events involve characters - should not output involved characters`() {
        val characters = List(5) { makeCharacter(projectId = scene.projectId) }
            .onEach(characterRepository::givenCharacter)
        storyEventRepository.givenStoryEvent(makeStoryEvent(sceneId = scene.id).withCharacterInvolved(characters[2]).storyEvent)
        sceneRepository.givenScene(scene)

        val response = listAvailableCharacters().getOrThrow()

        response.sceneId.shouldBeEqualTo(scene.id)
        response.shouldHaveSize(4)
        response.map { it.characterId }.shouldContainSame((characters - characters[2]).map { it.id.uuid })
    }

    private fun listAvailableCharacters(): Result<AvailableCharactersToAddToScene> = runBlocking {
        val useCase: ListAvailableCharactersToIncludeInScene =
            ListAvailableCharactersToIncludeInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
        var response: Result<AvailableCharactersToAddToScene> = Result.failure(Error("No response received"))
        useCase.invoke(scene.id) {
            response = Result.success(it)
        }.mapCatching { response.getOrThrow() }
    }

}