package com.soyle.stories.usecase.storyevent.involvedCharacters

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.domain.storyevent.character.exceptions.DuplicateInvolvedCharacterOperationException
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.shared.exceptions.RejectedUpdateException
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DynamicTest.dynamicTest

class `Remove Character From Story Event Unit Test` {

	private val storyEvent = makeStoryEvent()
	private val character = makeCharacter()

	private var characterRemoved: CharacterRemovedFromStoryEvent? = null

	private var storyEventPersistenceFailure: Throwable? = null
	private var updatedStoryEvent: StoryEvent? = null
	private var updatedScene: Scene? = null

	private val storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = {
		storyEventPersistenceFailure?.let { throw it }; updatedStoryEvent = it
	})
	private val characterRepository = CharacterRepositoryDouble()
	private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

	private fun removeCharacterFromStoryEvent() {
		val useCase: RemoveCharacterFromStoryEvent = RemoveCharacterFromStoryEventUseCase(
			storyEventRepository,
			sceneRepository
		)
		val characterRemovedProp = ::characterRemoved

		runBlocking {
			useCase(storyEvent.id, character.id, object : RemoveCharacterFromStoryEvent.OutputPort {
				override suspend fun characterRemovedFromStoryEvent(characterRemoved: CharacterRemovedFromStoryEvent) {
					characterRemovedProp.set(characterRemoved)
				}
			})
		}
	}

	@Test
	fun `when story event doesn't exist, should throw error`() {
		characterRepository.givenCharacter(character)

		val error = assertThrows<StoryEventDoesNotExist> {
			removeCharacterFromStoryEvent()
		}
		error.storyEventId.mustEqual(storyEvent.id.uuid)
		assertNull(characterRemoved)
		assertNull(updatedStoryEvent)
		assertNull(updatedScene)
	}

	@Nested
	inner class `Given Story Event Exists` {

		init {
			storyEventRepository.givenStoryEvent(storyEvent)
		}

		@Test
		fun `when character is not involved in story event, should throw error`() {
			val error = assertThrows<DuplicateInvolvedCharacterOperationException> { removeCharacterFromStoryEvent() }
			error.storyEventId.mustEqual(storyEvent.id)
			error.characterId.mustEqual(character.id)

			assertNull(characterRemoved)
			assertNull(updatedStoryEvent)
			assertNull(updatedScene)
		}

		@Nested
		inner class `Given Story Event Involves Character` {

			val storyEventWithCharacter = storyEvent.withCharacterInvolved(character)
				.storyEvent.also(storyEventRepository::givenStoryEvent)

			@Test
			fun `should produce character removed event`() {
				removeCharacterFromStoryEvent()

				with(characterRemoved!!) {
					storyEventId.mustEqual(storyEvent.id)
					characterId.mustEqual(character.id)
				}
			}

			@Test
			fun `should update story event to involve character`() {
				removeCharacterFromStoryEvent()

				with(updatedStoryEvent!!) {
					id.mustEqual(storyEvent.id)
					involvedCharacters.getEntityById(character.id).shouldBeNull()
				}
			}

			@Test
			fun `should not update scene`() {
				removeCharacterFromStoryEvent()

				assertNull(updatedScene)
			}

			@Nested
			inner class `Story Event Repository Rejects Update` {

				init {
					storyEventPersistenceFailure = Error("Intentional failure")
				}

				@Test
				fun `should throw error`() {
					val error = assertThrows<RejectedUpdateException> { removeCharacterFromStoryEvent() }
					error.cause.mustEqual(storyEventPersistenceFailure)
				}

				@Test
				fun `should not update story event or scene`() {
					assertThrows<Throwable> { removeCharacterFromStoryEvent() }

					assertNull(characterRemoved)
					assertNull(updatedStoryEvent)
					assertNull(updatedScene)
				}

			}

		}

	}

}