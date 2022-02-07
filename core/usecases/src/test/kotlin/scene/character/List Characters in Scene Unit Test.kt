package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.givenCharacter
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.storyevent.character.InvolvedCharacter
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.validation.entitySetOf
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import com.soyle.stories.usecase.scene.character.list.ListCharactersInScene
import com.soyle.stories.usecase.scene.character.list.ListCharactersInSceneUseCase
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene
import kotlinx.coroutines.*
import org.amshove.kluent.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Characters in Scene Unit Test` {

    // Prerequisites
    private val scene = makeScene()

    private val sceneRepository = SceneRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()

    @Test
    fun `Scene Does not Exist`() {
        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val error = result.exceptionOrNull()!!
        error.shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
    }

    @Test
    fun `Scene Has no Characters`() {
        // given
        sceneRepository.givenScene(scene)
        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.sceneId.shouldBeEqualTo(scene.id)
        response.sceneName.shouldBeEqualTo(scene.name.value)
        response.items.shouldBeEmpty()
    }

    @Test
    fun `Characters in Scene no Longer exist`() {
        // given
        val character = makeCharacter()
        sceneRepository.givenScene(scene.givenCharacter(character))
        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.sceneId.shouldBeEqualTo(scene.id)
        response.sceneName.shouldBeEqualTo(scene.name.value)
        response.items.shouldBeEmpty()
    }

    @Test
    fun `Characters in Scene no Longer in Project`() {
        // given
        val (character) = makeCharacter().removedFromStory()
        characterRepository.givenCharacter(character)
        sceneRepository.givenScene(scene.givenCharacter(character))
        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.sceneId.shouldBeEqualTo(scene.id)
        response.sceneName.shouldBeEqualTo(scene.name.value)
        with(response.items.single()) {
            characterId.shouldBeEqualTo(character.id)
            characterName.shouldBeEqualTo(character.displayName.value)
            project.shouldBeNull()
        }
    }

    @Test
    fun `Included Characters have Backing Character`() {
        // given
        val character = makeCharacter().also(characterRepository::givenCharacter)
        sceneRepository.givenScene(scene.givenCharacter(character))
        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.shouldHaveExactly(listOf(character))
        response.items.forEach { it.scene.shouldBeEqualTo(scene.id) }
        response.items.forEach { it.roleInScene.shouldBeNull() }
        response.items.forEach { it.sources.shouldBeEmpty() }
        response.items.forEach { it.isExplicit.shouldBeTrue() }
    }

    @Test
    fun `Included Characters have Roles`() {
        // given
        val characters = List(5) { makeCharacter() }.onEach(characterRepository::givenCharacter)
        characters.fold(scene) { scene, character -> scene.givenCharacter(character) }
            .withCharacter(characters[2].id)!!.assignedRole(RoleInScene.IncitingCharacter).scene
            .withCharacter(characters[3].id)!!.assignedRole(RoleInScene.OpponentCharacter).scene
            .withCharacter(characters[0].id)!!.assignedRole(RoleInScene.OpponentCharacter).scene
            .also(sceneRepository::givenScene)
        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.character(characters[0].id)!!.roleInScene.mustEqual(RoleInScene.OpponentCharacter)
        response.character(characters[1].id)!!.roleInScene.shouldBeNull()
        response.character(characters[2].id)!!.roleInScene.mustEqual(RoleInScene.IncitingCharacter)
        response.character(characters[3].id)!!.roleInScene.mustEqual(RoleInScene.OpponentCharacter)
        response.character(characters[4].id)!!.roleInScene.shouldBeNull()
    }

    @Test
    fun `Story Events Covered by Scene Involve Characters that no longer exist`() {
        sceneRepository.givenScene(scene)
        List(3) {
            makeStoryEvent(sceneId = scene.id, includedCharacterIds = entitySetOf(
                InvolvedCharacter(Character.Id(), ""),
                InvolvedCharacter(Character.Id(), "")
            )).also(storyEventRepository::givenStoryEvent)
        }.flatMap { it.involvedCharacters }

        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.sceneId.mustEqual(scene.id)
        response.items.shouldBeEmpty()
    }

    @Test
    fun `Story Events Covered by Scene Involve Characters`() {
        sceneRepository.givenScene(scene)
        val characters = List(6) { makeCharacter() }
            .onEach(characterRepository::givenCharacter)
        repeat(3) {
            makeStoryEvent(sceneId = scene.id, includedCharacterIds = entitySetOf(
                InvolvedCharacter(characters[(it * 2)].id, ""),
                InvolvedCharacter(characters[(it * 2) + 1].id, "")
            )).also(storyEventRepository::givenStoryEvent)
        }
        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.sceneId.mustEqual(scene.id)
        response.shouldHaveExactly(characters)
        response.items.forEach { it.roleInScene.shouldBeNull() }
        response.items.forEach { it.sources.shouldNotBeEmpty() }
        response.items.forEach { it.isExplicit.shouldBeFalse() }
    }

    @Test
    fun `Included Characters are Also Involved in Covered Story Events`() {
        val characters = List(6) { makeCharacter() }
            .onEach(characterRepository::givenCharacter)
        repeat(3) {
            makeStoryEvent(sceneId = scene.id, includedCharacterIds = entitySetOf(
                InvolvedCharacter(characters[(it * 2)].id, ""),
                InvolvedCharacter(characters[(it * 2) + 1].id, "")
            )).also(storyEventRepository::givenStoryEvent)
        }
        val includedCharacters = characters.shuffled().take(3)
        includedCharacters.fold(scene) { scene, character ->
            scene.givenCharacter(character)
                .withCharacter(character.id)!!.assignedRole(RoleInScene.OpponentCharacter).scene
                .withCharacter(character.id)!!.desireChanged("Desire for ${character.displayName}").scene
                .withCharacter(character.id)!!.motivationChanged("Motivation for ${character.displayName}").scene
        }.also(sceneRepository::givenScene)
        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.sceneId.mustEqual(scene.id)
        response.shouldHaveExactly(characters)
        includedCharacters.forEach {
            val responseCharacter = response.character(it.id)!!
            responseCharacter.roleInScene.shouldBeEqualTo(RoleInScene.OpponentCharacter)
        }
    }

    @Test
    fun `Story Events Covered by Scene Involve Characters with Inherited Motivations`() {
        val characters = List(6) { makeCharacter() }
            .onEach(characterRepository::givenCharacter)

        repeat(3) {
            makeStoryEvent(sceneId = scene.id, includedCharacterIds = entitySetOf(
                InvolvedCharacter(characters[(it * 2)].id, ""),
                InvolvedCharacter(characters[(it * 2) + 1].id, "")
            )).also(storyEventRepository::givenStoryEvent)
        }

        val previousScene = makeScene(projectId = scene.projectId)
            .withCharacterIncluded(characters[1]).scene
            .withCharacter(characters[1].id)!!.motivationChanged("Initial Motivation").scene

        sceneRepository.givenScene(previousScene)
        sceneRepository.givenScene(scene)
        sceneRepository.sceneOrders[scene.projectId] =
            SceneOrder.reInstantiate(scene.projectId, listOf(previousScene.id, scene.id))

        // when
        val result = runCatching(::listCharactersInScene)
        // then
        val response = result.getOrThrow()
        response.sceneId.mustEqual(scene.id)
        response.shouldHaveExactly(characters)
    }

    private fun CharactersInScene.shouldHaveExactly(characters: List<Character>) {
        sceneId.mustEqual(scene.id)
        items.size.mustEqual(characters.size)
        items.map { it.characterId }.toSet().mustEqual(characters.map { it.id }.toSet())
        items.forEach { includedCharacter ->
            with(includedCharacter) {
                characterName.mustEqual(characters.single { it.id == characterId }.displayName.value)
            }
        }
    }

    private fun CharactersInScene.character(characterId: Character.Id): CharacterInSceneItem? =
        items.find { it.characterId == characterId }

    private fun listCharactersInScene(): CharactersInScene {
        val useCase: ListCharactersInScene = ListCharactersInSceneUseCase(sceneRepository, characterRepository, storyEventRepository)
        lateinit var response: CharactersInScene
        runBlocking {
            useCase.invoke(scene.id) { response = it }
        }
        return response
    }

}