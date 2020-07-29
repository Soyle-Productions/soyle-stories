package com.soyle.stories.storyevent.usecases

import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.doubles.StoryEventRepositoryDouble
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.storyEventDoesNotExist
import com.soyle.stories.storyevent.usecases.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.storyevent.usecases.getStoryEventDetails.GetStoryEventDetailsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class GetStoryEventDetailsUnitTest {

	private val storyEventId = StoryEvent.Id().uuid
	private val storyEventName = "My Story Event"
	private val locationId = Location.Id().uuid
	private val includedCharacterIds = List(5) { Character.Id().uuid }

	private val NoStoryEventsExist = mapOf<UUID, Pair<UUID?, List<UUID>>>()
	private val NoStoryEventsWithId = List(5) { StoryEvent.Id().uuid to Pair<UUID?, List<UUID>>(null, listOf()) }.toMap()
	private val NoCharacters = listOf<UUID>()

	private fun assertGetStoryEventDetails(storyEventIds: Map<UUID, Pair<UUID?, List<UUID>>>, expectedResult: (Any?) -> Unit)
	{
		val executor = UseCaseExecutor(storyEventIds, storyEventName)
		executor.execute(storyEventId)
		expectedResult(executor.result)
	}

	@Test
	fun `story event doesn't exist`() {
		assertGetStoryEventDetails(NoStoryEventsExist, storyEventDoesNotExist(storyEventId))
		assertGetStoryEventDetails(NoStoryEventsWithId, storyEventDoesNotExist(storyEventId))
	}

	@Test
	fun `basic story event`() {
		assertGetStoryEventDetails(
		  mapOf(storyEventId to Pair(null, NoCharacters)),
		  responseModel()
		)
	}

	@Test
	fun `story event with location`() {
		assertGetStoryEventDetails(
		  mapOf(storyEventId to Pair(locationId, NoCharacters)),
		  responseModel(locationId)
		)
	}

	@Test
	fun `story event with characters`() {
		assertGetStoryEventDetails(
		  mapOf(storyEventId to Pair(locationId, includedCharacterIds)),
		  responseModel(locationId, includedCharacterIds)
		)
	}

	private fun responseModel(expectedLocationId: UUID? = null, expectedCharacterIds: List<UUID> = emptyList()): (Any?) -> Unit = { actual: Any? ->
		actual as GetStoryEventDetails.ResponseModel
		assertEquals(storyEventId, actual.storyEventId)
		assertEquals(storyEventName, actual.storyEventName)
		assertEquals(expectedLocationId, actual.locationId)
		assertEquals(expectedCharacterIds.toSet(), actual.includedCharacterIds.toSet())
	}

	private class UseCaseExecutor(storyEventIds: Map<UUID, Pair<UUID?, List<UUID>>>, private val storyEventName: String)
	{
		private val repository = makeRepository(storyEventIds)

		var update: Any? = null
			private set
		var result: Any? = null
			private set

		fun execute(storyEventId: UUID) {
			val useCase: GetStoryEventDetails = GetStoryEventDetailsUseCase(repository)
			runBlocking {
				useCase.invoke(storyEventId, object : GetStoryEventDetails.OutputPort {
					override fun receiveGetStoryEventDetailsFailure(failure: StoryEventException) {
						result = failure
					}

					override fun receiveGetStoryEventDetailsResponse(response: GetStoryEventDetails.ResponseModel) {
						result = response
					}
				})
			}
		}

		private fun makeRepository(storyEventIds: Map<UUID, Pair<UUID?, List<UUID>>>): StoryEventRepository
		{
			return StoryEventRepositoryDouble(
			  initialStoryEvents = storyEventIds.map { (it, links) ->
				  val (location, characterIds) = links
				  StoryEvent(StoryEvent.Id(it), storyEventName, Project.Id(), null, null, location?.let(Location::Id), characterIds.map(Character::Id))
			  },
			  onUpdateStoryEvent = { update = it }
			)
		}

		private fun makeCharacterRepository(characterIds: List<UUID>): CharacterRepository
		{
			return CharacterRepositoryDouble(initialCharacters = characterIds.map {
                makeCharacter(Character.Id(it), Project.Id(), "Bob")
            })
		}
	}

}