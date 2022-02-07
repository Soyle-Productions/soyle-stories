package com.soyle.stories.usecase.storyevent.involvedCharacters

import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.GetAvailableCharactersToInvolveInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.GetAvailableCharactersToInvolveInStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class `Get Available Characters to Involve Unit Test` {

    // Prerequisites
    private val storyEvent = makeStoryEvent()

    // repositories
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()

    @Test
    fun `given story event does not exist - should return error`() {
        val result = getAvailableCharacters()

        val error = result.exceptionOrNull()!!
        error.shouldBeEqualTo(StoryEventDoesNotExist(storyEvent.id.uuid))
    }

    @Test
    fun `given story event exists - should output available characters for story event`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        val result = getAvailableCharacters()

        val availableCharacters = result.getOrThrow()
        availableCharacters.storyEvent.shouldBeEqualTo(storyEvent.id)
    }

    @Test
    fun `given no characters exist - output should be empty`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        val result = getAvailableCharacters()

        val availableCharacters = result.getOrThrow()
        availableCharacters.allAvailableElements.shouldBeEmpty()
    }

    @Test
    fun `given characters exist - output should contain all of them`() {
        storyEventRepository.givenStoryEvent(storyEvent)
        val characters = List(5) {
            makeCharacter(projectId = storyEvent.projectId).also(characterRepository::givenCharacter)
        }

        val result = getAvailableCharacters()

        val availableCharacters = result.getOrThrow()
        availableCharacters.allAvailableElements.shouldHaveSize(characters.size)
        availableCharacters.allAvailableElements.map { it.entityId.id }.toSet()
            .shouldBeEqualTo(characters.map{ it.id }.toSet())
    }

    @Test
    fun `given story event involves characters - output should not contain involved characters`() {
        val characters = List(5) {
            makeCharacter(projectId = storyEvent.projectId).also(characterRepository::givenCharacter)
        }
        val involvedCharacters = characters.shuffled().take(3)
        involvedCharacters.fold(storyEvent) { storyEvent, character -> storyEvent.withCharacterInvolved(character).storyEvent }
            .let(storyEventRepository::givenStoryEvent)

        val result = getAvailableCharacters()

        val availableCharacters = result.getOrThrow()
        availableCharacters.allAvailableElements.shouldHaveSize(characters.size - involvedCharacters.size)
        availableCharacters.allAvailableElements.map { it.entityId.id }.toSet()
            .shouldBeEqualTo(characters.map{ it.id }.toSet() - involvedCharacters.map { it.id }.toSet())
    }

    @Test
    fun `given characters have multiple names - output should have items for each name`() {
        storyEventRepository.givenStoryEvent(storyEvent)
        val characters = List(5) {
            makeCharacter(projectId = storyEvent.projectId)
                .withName(characterName()).character
                .withName(characterName()).character
                .also(characterRepository::givenCharacter)
        }

        val result = getAvailableCharacters()

        val availableCharacters = result.getOrThrow()
        availableCharacters.allAvailableElements.shouldHaveSize(characters.size * 3)
    }

    private fun getAvailableCharacters(): Result<AvailableCharactersToInvolveInStoryEvent> {
        val useCase: GetAvailableCharactersToInvolveInStoryEvent =
            GetAvailableCharactersToInvolveInStoryEventUseCase(storyEventRepository, characterRepository)
        return runBlocking {
            lateinit var response: AvailableCharactersToInvolveInStoryEvent
            val error = useCase.invoke(storyEvent.id) {
                response = it
            }
            if (error != null) Result.failure(error)
            else Result.success(response)
        }
    }

}