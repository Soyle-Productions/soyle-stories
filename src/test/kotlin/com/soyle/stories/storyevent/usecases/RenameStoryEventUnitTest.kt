package com.soyle.stories.storyevent.usecases

import com.soyle.stories.character.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.doubles.StoryEventRepositoryDouble
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.storyEventDoesNotExist
import com.soyle.stories.storyevent.usecases.renameStoryEvent.RenameStoryEvent
import com.soyle.stories.storyevent.usecases.renameStoryEvent.RenameStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class RenameStoryEventUnitTest {

	private val NoStoryEvents = emptyList<UUID>()
	private val NoStoryEventsWithId = listOf(StoryEvent.Id().uuid)

	private val storyEventId = StoryEvent.Id().uuid
	private val newName = "Updated Story Event Name ${UUID.randomUUID().toString().last()}"

	private fun assertRenameStoryEvent(storyEventIds: List<Any>, updateAssertion: (Any?) -> Unit, resultAssertion: (Any?) -> Unit)
	{
		val useCaseExecutor = UseCaseExecutor(storyEventIds)
		useCaseExecutor.execute(storyEventId, newName)
		updateAssertion(useCaseExecutor.update)
		resultAssertion(useCaseExecutor.result)
	}

	@Test
	fun `story event doesn't exist`() {
		assertRenameStoryEvent(NoStoryEvents, noUpdate(), storyEventDoesNotExist(storyEventId))
		assertRenameStoryEvent(NoStoryEventsWithId, noUpdate(), storyEventDoesNotExist(storyEventId))
	}

	@Test
	fun exists() {
		assertRenameStoryEvent(listOf(storyEventId), updated(storyEventId, newName), responseModel(storyEventId, newName))
	}

	@Test
	fun `name is identical`() {
		assertRenameStoryEvent(listOf(storyEventId).withName(newName), noUpdate(), responseModel(storyEventId, newName))
	}

	private fun noUpdate() = { update: Any? ->
		assertNull(update)
	}

	private fun updated(storyEventId: UUID, newName: String) = { update: Any? ->
		update as StoryEvent
		assertEquals(storyEventId, update.id.uuid)
		assertEquals(newName, update.name)
	}

	private fun responseModel(storyEventId: UUID, expectedName: String) = { actual: Any? ->
		actual as RenameStoryEvent.ResponseModel
		assertEquals(storyEventId, actual.storyEventId)
		assertEquals(expectedName, actual.newName)
	}

	private fun List<UUID>.withName(name: String) = map { it to name }

	private class UseCaseExecutor(storyEventIds: List<Any>)
	{
		private val repository = makeRepository(storyEventIds)

		var update: Any? = null
		private set
		var result: Any? = null
			private set

		fun execute(storyEventId: UUID, newName: String) {
			val useCase: RenameStoryEvent = RenameStoryEventUseCase(repository)
			runBlocking {
				useCase.invoke(storyEventId, newName, object : RenameStoryEvent.OutputPort {
					override fun receiveRenameStoryEventFailure(failure: StoryEventException) {
						result = failure
					}

					override fun receiveRenameStoryEventResponse(response: RenameStoryEvent.ResponseModel) {
						result = response
					}
				})
			}
		}

		private fun makeRepository(storyEventIds: List<Any>): StoryEventRepository
		{
			return StoryEventRepositoryDouble(
			  initialStoryEvents = storyEventIds.map { any ->
				  val it = (any as? UUID) ?: ((any as? Pair<*, *>)?.first as? UUID) ?: UUID.randomUUID()
				  val name = ((any as? Pair<*, *>)?.second as? String) ?: "Some Name"
				  StoryEvent(StoryEvent.Id(it), name, Project.Id(), null, null, null, listOf())
			  },
			  onUpdateStoryEvent = { update = it }
			)
		}

		private fun makeCharacterRepository(characterIds: List<UUID>): CharacterRepository
		{
			return CharacterRepositoryDouble(initialCharacters = characterIds.map {
				Character(Character.Id(it),  Project.Id(), "Bob")
			})
		}
	}

}