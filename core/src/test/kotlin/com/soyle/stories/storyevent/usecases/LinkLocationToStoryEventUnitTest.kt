package com.soyle.stories.storyevent.usecases

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.makeLocation
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import com.soyle.stories.storyevent.doubles.StoryEventRepositoryDouble
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEvent
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class LinkLocationToStoryEventUnitTest {

	val NoStoryEvents = emptyList<Pair<UUID, UUID?>>()
	val NoStoryEventWithId = listOf<Pair<UUID, UUID?>>(UUID.randomUUID() to null)
	val NoLocations = emptyList<UUID>()
	val NoLocationWithId = listOf(UUID.randomUUID())
	val AnyLocation = NoLocationWithId
	val NoOriginalLocation: UUID? = null

	private val storyEventId = StoryEvent.Id().uuid
	private val locationId = Location.Id().uuid
	private val projectId = Project.Id()

	private var updatedStoryEvent: StoryEvent? = null
	private var result: Any? = null

	private fun linkLocationToStoryEvent(storyEventIds: List<Pair<UUID, UUID?>>, locationIds: List<UUID>): Any? {
		val useCase: LinkLocationToStoryEvent = LinkLocationToStoryEventUseCase(makeRepository(storyEventIds), makeLocationRepo(locationIds))
		executeUseCase(useCase)
		return result
	}

	private fun unlinkLocationToStoryEvent(storyEventIds: List<UUID>): Any? {
		val useCase: LinkLocationToStoryEvent = LinkLocationToStoryEventUseCase(makeRepository(storyEventIds.map { it to locationId }), makeLocationRepo(AnyLocation))
		executeUseCase(useCase, withLocation = null)
		return result
	}

	@Test
	fun `story event doesn't exist`() {
		assertEquals(storyEventId, (linkLocationToStoryEvent(NoStoryEvents, AnyLocation) as StoryEventDoesNotExist).storyEventId)
		assertEquals(storyEventId, (linkLocationToStoryEvent(NoStoryEventWithId, AnyLocation) as StoryEventDoesNotExist).storyEventId)
	}

	@Test
	fun `location doesn't exist`() {
		assertEquals(locationId, (linkLocationToStoryEvent(listOf(storyEventId to NoOriginalLocation), NoLocations) as LocationDoesNotExist).locationId)
		assertEquals(locationId, (linkLocationToStoryEvent(listOf(storyEventId to NoOriginalLocation), NoLocationWithId) as LocationDoesNotExist).locationId)
	}

	@Test
	fun `both exist`() {
		linkLocationToStoryEvent(listOf(storyEventId to NoOriginalLocation), listOf(locationId)) as LinkLocationToStoryEvent.ResponseModel
		assertResult(storyEventId, locationId, null)
		assertEquals(locationId, updatedStoryEvent?.linkedLocationId?.uuid)
	}

	@Test
	fun `clear location`() {
		unlinkLocationToStoryEvent(listOf(storyEventId)) as LinkLocationToStoryEvent.ResponseModel
		assertResult(storyEventId, null, locationId)
		assertNull(updatedStoryEvent!!.linkedLocationId)
	}

	@Test
	fun `replace location`() {
		val oldLocationId = Location.Id().uuid
		linkLocationToStoryEvent(listOf(storyEventId to oldLocationId), listOf(locationId)) as LinkLocationToStoryEvent.ResponseModel
		assertResult(storyEventId, locationId, oldLocationId)
		assertEquals(locationId, updatedStoryEvent?.linkedLocationId?.uuid)
	}

	private fun makeRepository(storyEvents: List<Pair<UUID, UUID?>>): StoryEventRepository
	{
		return StoryEventRepositoryDouble(initialStoryEvents = storyEvents.map { (it, linkedTo) ->
			StoryEvent(StoryEvent.Id(it), "", projectId, null, null, linkedTo?.let(Location::Id), listOf())
		}, onUpdateStoryEvent = { updatedStoryEvent = it })
	}

	private fun makeLocationRepo(locationIds: List<UUID>): LocationRepository
	{
		return LocationRepositoryDouble(initialLocations = locationIds.map {
			makeLocation(id = Location.Id(it), projectId = projectId)
		})
	}

	private fun executeUseCase(useCase: LinkLocationToStoryEvent, withLocation: Unit? = Unit)
	{
		val inputLocationId = withLocation?.let { locationId }
		runBlocking {
			useCase.invoke(storyEventId, inputLocationId, object : LinkLocationToStoryEvent.OutputPort {
				override fun receiveLinkLocationToStoryEventResponse(response: LinkLocationToStoryEvent.ResponseModel) {
					result = response
				}

				override fun receiveLinkLocationToStoryEventFailure(failure: Exception) {
					result = failure
				}
			})
		}
	}

	private fun assertResult(expectedStoryEventId: UUID, expectedLocationId: UUID?, expectedUnLinkedLocationId: UUID?)
	{
		val result = result as LinkLocationToStoryEvent.ResponseModel
		assertEquals(expectedStoryEventId, result.storyEventId)
		assertEquals(expectedLocationId, result.locationId)
		assertEquals(expectedUnLinkedLocationId, result.unlinkedLocationId)
	}

}