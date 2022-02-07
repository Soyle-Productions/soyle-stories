package com.soyle.stories.usecase.storyevent.involvedCharacters

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.domain.storyevent.character.exceptions.DuplicateInvolvedCharacterOperationException
import com.soyle.stories.domain.storyevent.character.exceptions.storyEventAlreadyWithoutCharacter
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.shared.exceptions.RejectedUpdateException
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.character.remove.*
import com.soyle.stories.usecase.storyevent.coverage.uncover.GetPotentialChangesFromUncoveringStoryEvent
import com.soyle.stories.usecase.storyevent.coverage.uncover.GetPotentialChangesFromUncoveringStoryEventUseCase
import com.soyle.stories.usecase.storyevent.coverage.uncover.PotentialChangesFromUncoveringStoryEvent
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DynamicTest.dynamicTest

class `Get Potential Changes of Removing Character From Story Event Unit Test` {

	private val character = makeCharacter()
	private val scene = makeScene()
	private val storyEvent = makeStoryEvent().coveredByScene(scene.id)
		.storyEvent.withCharacterInvolved(character)
		.storyEvent

	private val storyEventRepository = StoryEventRepositoryDouble()
	private val characterRepository = CharacterRepositoryDouble()
	private val sceneRepository = SceneRepositoryDouble()

	init {
	    storyEventRepository.givenStoryEvent(storyEvent)
		characterRepository.givenCharacter(character)
		sceneRepository.givenScene(scene)
	}

	@Nested
	inner class `Story Event Must Exist` {

		init { storyEventRepository.storyEvents.remove(storyEvent.id) }

		@Test
		fun `given story event doesn't exist - should throw error`() {
			val result = getPotentialChanges()

			result.exceptionOrNull().shouldBeEqualTo(StoryEventDoesNotExist(storyEvent.id.uuid))
		}

	}

	@Nested
	inner class `Character Must Exist` {

		init { characterRepository.characters.remove(character.id) }

		@Test
		fun `given character doesn't exist - should throw error`() {
			val result = getPotentialChanges()

			result.exceptionOrNull().shouldBeEqualTo(CharacterDoesNotExist(character.id))
		}

	}

	@Nested
	inner class `Story Event Must Involve Character` {

		init { storyEvent.withCharacterRemoved(character.id).storyEvent.also(storyEventRepository::givenStoryEvent) }

		@Test
		fun `given story event does not involve character - should throw error`() {
			val result = getPotentialChanges()

			result.exceptionOrNull().shouldBeEqualTo(storyEventAlreadyWithoutCharacter(storyEvent.id, character.id))
		}

	}

	@Nested
	inner class `Removing Character from Story Event may not have any Effects` {

		@AfterEach
		fun `output should be empty`() {
			val result = getPotentialChanges()

			result.getOrThrow().shouldBeEmpty()
		}

		@Test
		fun `story event is not covered by scene`() {
			storyEvent.withoutCoverage()
				.storyEvent.also(storyEventRepository::givenStoryEvent)
		}

		@Test
		fun `scene does not exist`() {
			sceneRepository.scenes.remove(scene.id)
		}

		@Test
		fun `scene explicitly includes character`() {
			scene.withCharacterIncluded(character)
				.scene.also(sceneRepository::givenScene)
		}

		@Test
		fun `other covered story event involves character`() {
			makeStoryEvent().coveredByScene(scene.id)
				.storyEvent.withCharacterInvolved(character)
				.storyEvent.also(storyEventRepository::givenStoryEvent)
		}

	}

	@Test
	fun `should output implicit character removed from scene effect`() {
		val result = getPotentialChanges()

		val effects = result.getOrThrow()
		effects.shouldHaveSize(1)
		effects.single().let {
			it.character.shouldBeEqualTo(character.id)
			it.characterName.shouldBeEqualTo(character.displayName.value)
			it.scene.shouldBeEqualTo(scene.id)
			it.sceneName.shouldBeEqualTo(scene.name.value)
		}
	}

	private fun getPotentialChanges(): Result<PotentialChangesOfRemovingCharacterFromStoryEvent> {
		val useCase: GetPotentialChangesOfRemovingCharacterFromStoryEvent = GetPotentialChangesOfRemovingCharacterFromStoryEventUseCase(
			storyEventRepository, characterRepository, sceneRepository
		)
		var result = Result.failure<PotentialChangesOfRemovingCharacterFromStoryEvent>(Error("Response not received"))
		return runCatching {
			runBlocking {
				useCase.invoke(storyEvent.id, character.id) { result = Result.success(it) }
			}
		}.mapCatching { result.getOrThrow() }
	}


}