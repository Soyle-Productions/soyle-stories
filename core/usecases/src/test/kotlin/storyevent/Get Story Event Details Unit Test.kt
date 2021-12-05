package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetailsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Get Story Event Details Unit Test` {

	// Summary
	/** Gets the extrapolated data about a specified story event */

	// Preconditions
	/** The project exists */
	private val projectId = Project.Id()
	/** The story event exists */
	private val storyEvent = makeStoryEvent(projectId = projectId)

	// post conditions
	/** outputs story event details */
	private var responseModel: GetStoryEventDetails.ResponseModel? = null
	private val storyEventRepository = StoryEventRepositoryDouble()

	// Use Case
	val useCase: GetStoryEventDetails = GetStoryEventDetailsUseCase(storyEventRepository)
	private fun getStoryEventDetails() {
		runBlocking {
			useCase.invoke(storyEvent.id) {
				responseModel = it
			}
		}
	}

	@Test
	fun `given story event does not exist _ should throw error`() {
		val error = assertThrows<StoryEventDoesNotExist> { getStoryEventDetails() }
		error.storyEventId.mustEqual(storyEvent.id.uuid)
	}

	@Nested
	inner class `Given Story Event Exists` {

		init {
		    storyEventRepository.givenStoryEvent(storyEvent)
		}

		@Test
		fun `should output story event name`() {
			getStoryEventDetails()
			responseModel!!.storyEventName.mustEqual(storyEvent.name.value)
		}

		@Test
		fun `should not output location`() {
			getStoryEventDetails()
			assertNull(responseModel!!.locationId)
		}

		@Test
		fun `should not output any character`() {
			getStoryEventDetails()
			responseModel!!.includedCharacterIds.size.mustEqual(0)
		}

		@Nested
		inner class `Given Story Event has a Location` {

			private val locationId = Location.Id()
			init {
			    storyEventRepository.givenStoryEvent(storyEvent.withLocationId(locationId))
			}

			@Test
			fun `should output location id`() {
				getStoryEventDetails()
				responseModel!!.locationId.mustEqual(locationId)
			}

		}

		@Nested
		inner class `Given Story Event has Included Characters` {

			private val characterIds = List(14) { Character.Id() }
			init {
				characterIds.fold(storyEvent) { event, character ->
					event.withCharacterInvolved(character).storyEvent
				}
					.let(storyEventRepository::givenStoryEvent)
			}

			@Test
			fun `should output character ids`() {
				getStoryEventDetails()
				responseModel!!.includedCharacterIds.size.mustEqual(14)
				responseModel!!.includedCharacterIds.toSet().mustEqual(characterIds.toSet())
			}

		}

	}
}