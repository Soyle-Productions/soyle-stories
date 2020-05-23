package com.soyle.stories.storyevent.usecases

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.characterNotInStoryEvent
import com.soyle.stories.storyevent.doubles.StoryEventRepositoryDouble
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.storyEventDoesNotExist
import com.soyle.stories.storyevent.usecases.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent
import com.soyle.stories.storyevent.usecases.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class RemoveCharacterFromStoryEventUnitTest {

	private val NoStoryEvents = emptyList<UUID>()
	private val NoStoryEventsWithId = listOf(StoryEvent.Id().uuid)

	private val storyEventId = StoryEvent.Id().uuid
	private val characterId = Character.Id().uuid

	@JvmName("assertRemoveCharacterFromStoryEventWithoutIncludedCharacters")
	private fun assertRemoveCharacterFromStoryEvent(storyEventIds: List<UUID>, updateAssertion: (Any?) -> Unit, resultAssertion: (Any?) -> Unit)
	{
		assertRemoveCharacterFromStoryEvent(storyEventIds.map { it to listOf<UUID>() }, updateAssertion, resultAssertion)
	}


	@JvmName("assertRemoveCharacterFromStoryEventWithIncludedCharacters")
	private fun assertRemoveCharacterFromStoryEvent(storyEventIds: List<Pair<UUID, List<UUID>>>, updateAssertion: (Any?) -> Unit, resultAssertion: (Any?) -> Unit)
	{
		val useCaseExecutor = UseCaseExecutor(storyEventIds)
		useCaseExecutor.execute(storyEventId, characterId)
		updateAssertion(useCaseExecutor.update)
		resultAssertion(useCaseExecutor.result)
	}

	@Test
	fun `story event doesn't exist`() {
		assertRemoveCharacterFromStoryEvent(NoStoryEvents, noUpdate(), storyEventDoesNotExist(storyEventId))
		assertRemoveCharacterFromStoryEvent(NoStoryEventsWithId, noUpdate(), storyEventDoesNotExist(storyEventId))
	}

	@Test
	fun `character not in story event`() {
		assertRemoveCharacterFromStoryEvent(listOf(storyEventId), noUpdate(), characterNotInStoryEvent(storyEventId, characterId))
	}

	@Test
	fun `character in story event`() {
		assertRemoveCharacterFromStoryEvent(listOf(storyEventId).withIncludedCharacter(characterId), updated(storyEventId, characterId), responseModel(storyEventId, characterId))
	}

	private fun noUpdate() = { update: Any? ->
		assertNull(update)
	}

	private fun updated(storyEventId: UUID, characterId: UUID) = { update: Any? ->
		update as StoryEvent
		assertEquals(storyEventId, update.id.uuid)
		assertFalse(update.includedCharacterIds.contains(Character.Id(characterId)))
	}


	private fun responseModel(storyEventId: UUID, characterId: UUID) = { actual: Any? ->
		actual as RemoveCharacterFromStoryEvent.ResponseModel
		assertEquals(storyEventId, actual.storyEventId)
		assertEquals(characterId, actual.removedCharacterId)
	}

	private fun List<UUID>.withIncludedCharacter(characterId: UUID) = map { it to listOf(characterId) }

	private class UseCaseExecutor(storyEventIds: List<Pair<UUID, List<UUID>>>)
	{
		private val repository = makeRepository(storyEventIds)

		var update: Any? = null
		private set
		var result: Any? = null
			private set

		fun execute(storyEventId: UUID, characterId: UUID) {
			val useCase: RemoveCharacterFromStoryEvent = RemoveCharacterFromStoryEventUseCase(repository)
			runBlocking {
				useCase.invoke(storyEventId, characterId, object : RemoveCharacterFromStoryEvent.OutputPort {
					override fun receiveRemoveCharacterFromStoryEventFailure(failure: StoryEventException) {
						result = failure
					}

					override fun receiveRemoveCharacterFromStoryEventResponse(response: RemoveCharacterFromStoryEvent.ResponseModel) {
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
	}

}