package com.soyle.stories.storyevent.usecases

import com.soyle.stories.character.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.storyevent.doubles.StoryEventRepositoryDouble
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.storyEventDoesNotExist
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class AddCharacterToStoryEventUnitTest {

	private val NoStoryEvents = emptyList<UUID>()
	private val NoStoryEventsWithId = listOf(StoryEvent.Id().uuid)
	private val NoCharacters = emptyList<UUID>()
	private val NoCharactersWithId = listOf(Character.Id().uuid)
	private val AnyCharacter = NoCharactersWithId

	private val storyEventId = StoryEvent.Id().uuid
	private val characterId = Character.Id().uuid

	@JvmName("assertAddCharacterToStoryEventWithoutIncludedCharacters")
	private fun assertAddCharacterToStoryEvent(storyEventIds: List<UUID>, characterIds: List<UUID>, updateAssertion: (Any?) -> Unit, resultAssertion: (Any?) -> Unit)
	{
		assertAddCharacterToStoryEvent(storyEventIds.map { it to listOf<UUID>() }, characterIds, updateAssertion, resultAssertion)
	}


	@JvmName("assertAddCharacterToStoryEventWithIncludedCharacters")
	private fun assertAddCharacterToStoryEvent(storyEventIds: List<Pair<UUID, List<UUID>>>, characterIds: List<UUID>, updateAssertion: (Any?) -> Unit, resultAssertion: (Any?) -> Unit)
	{
		val useCaseExecutor = UseCaseExecutor(storyEventIds, characterIds)
		useCaseExecutor.execute(storyEventId, characterId)
		updateAssertion(useCaseExecutor.update)
		resultAssertion(useCaseExecutor.result)
	}

	@Test
	fun `story event doesn't exist`() {
		assertAddCharacterToStoryEvent(NoStoryEvents, AnyCharacter, noUpdate(), storyEventDoesNotExist(storyEventId))
		assertAddCharacterToStoryEvent(NoStoryEventsWithId, AnyCharacter, noUpdate(), storyEventDoesNotExist(storyEventId))
	}

	@Test
	fun `character doesn't exist`() {
		assertAddCharacterToStoryEvent(listOf(storyEventId), NoCharacters, noUpdate(), characterDoesNotExist(characterId))
		assertAddCharacterToStoryEvent(listOf(storyEventId), NoCharactersWithId, noUpdate(), characterDoesNotExist(characterId))
	}

	@Test
	fun `both exist`() {
		assertAddCharacterToStoryEvent(listOf(storyEventId), listOf(characterId), updated(storyEventId, characterId), responseModel(storyEventId, characterId))
	}

	@Test
	fun `already included`() {
		assertAddCharacterToStoryEvent(listOf(storyEventId).withIncludedCharacter(characterId), listOf(characterId), noUpdate(), responseModel(storyEventId, characterId))
	}

	private fun noUpdate() = { update: Any? ->
		assertNull(update)
	}

	private fun updated(storyEventId: UUID, characterId: UUID) = { update: Any? ->
		update as StoryEvent
		assertEquals(storyEventId, update.id.uuid)
		assertTrue(update.includedCharacterIds.contains(Character.Id(characterId)))
	}

	private fun responseModel(storyEventId: UUID, characterId: UUID) = { actual: Any? ->
		actual as AddCharacterToStoryEvent.ResponseModel
		assertEquals(storyEventId, actual.storyEventId)
		assertEquals(characterId, actual.characterId)
	}

	private fun List<UUID>.withIncludedCharacter(characterId: UUID) = map { it to listOf(characterId) }

	private class UseCaseExecutor(storyEventIds: List<Pair<UUID, List<UUID>>>, characterIds: List<UUID>)
	{
		private val repository = makeRepository(storyEventIds)
		private val characterRepository = makeCharacterRepository(characterIds)

		var update: Any? = null
		private set
		var result: Any? = null
			private set

		fun execute(storyEventId: UUID, characterId: UUID) {
			val useCase: AddCharacterToStoryEvent = AddCharacterToStoryEventUseCase(repository, characterRepository)
			runBlocking {
				useCase.invoke(storyEventId, characterId, object : AddCharacterToStoryEvent.OutputPort {
					override fun receiveAddCharacterToStoryEventFailure(failure: Exception) {
						result = failure
					}

					override fun receiveAddCharacterToStoryEventResponse(response: AddCharacterToStoryEvent.ResponseModel) {
						result = response
					}
				})
			}
		}

		private fun makeRepository(storyEventIds: List<Pair<UUID, List<UUID>>>): StoryEventRepository
		{
			return StoryEventRepositoryDouble(
			  initialStoryEvents = storyEventIds.map { (it, characterIds) ->
				  StoryEvent(StoryEvent.Id(it), "", Project.Id(), null, null, null, characterIds.map(Character::Id))
			  },
			  onUpdateStoryEvent = { update = it }
			)
		}

		private fun makeCharacterRepository(characterIds: List<UUID>): CharacterRepository
		{
			return CharacterRepositoryDouble(initialCharacters = characterIds.map {
				Character(Character.Id(it),  Project.Id().uuid, "Bob")
			})
		}
	}

}