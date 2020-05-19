package com.soyle.stories.storyevent

import com.soyle.stories.DependentProperty
import com.soyle.stories.ReadOnlyDependentProperty
import com.soyle.stories.character.CharacterDriver
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.storyevent.StoryEventsDriver.interact
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventController
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import kotlinx.coroutines.runBlocking
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

object StoryEventsDriver : ApplicationTest() {

	fun storyEventCreated() = storyEventCreated(null, null)
	fun storyEventCreatedBefore(relative: String) = storyEventCreated(relative, true)
	fun storyEventCreatedAfter(relative: String) = storyEventCreated(relative, false)
	private fun storyEventCreated(relative: String?, direction: Boolean?)  = object : DependentProperty<StoryEvent>
	{
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps::givenProjectHasBeenOpened
		)

		override fun get(double: SoyleStoriesTestDouble): StoryEvent? {
			val scope = ProjectSteps.getProjectScope(double) ?: return null
			val repo = scope.get<StoryEventRepository>()
			return runBlocking {
				if (relative != null && direction != null) {
					val relativeEvent = repo.getStoryEventById(StoryEvent.Id(UUID.fromString(relative))) ?: return@runBlocking null
					if (direction) {
						relativeEvent.nextStoryEventId?.let { repo.getStoryEventById(it) }
					} else {
						relativeEvent.previousStoryEventId?.let { repo.getStoryEventById(it) }
					}
				} else {
					repo.listStoryEventsInProject(Project.Id(scope.projectId)).firstOrNull()
				}
			}
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			interact {
				val controller = scope.get<CreateStoryEventController>()
				if (relative != null && direction != null) {
					if (direction)  controller.createStoryEventBefore("Story Event Name", relative)
					else  controller.createStoryEventAfter("Story Event Name", relative)
				} else controller.createStoryEvent("Story Event Name")
			}
		}
	}

	val storyEventsCreated = object : ReadOnlyDependentProperty<List<StoryEvent>>
	{
		override fun get(double: SoyleStoriesTestDouble): List<StoryEvent> {
			val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
			return runBlocking {
				scope.get<StoryEventRepository>().listStoryEventsInProject(Project.Id(scope.projectId))
			}
		}
		override fun check(double: SoyleStoriesTestDouble): Boolean = get(double).isNotEmpty()
	}

	fun storyEventsCreated(atLeast: Int) = object : DependentProperty<List<StoryEvent>>
	{
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps::givenProjectHasBeenOpened
		)

		override fun set(double: SoyleStoriesTestDouble) {
			dependencies.forEach { it(double) }
			val count = get(double).size
			if (count < atLeast)
			{
				repeat(atLeast - count) {
					whenSet(double)
				}
			}
		}

		override fun get(double: SoyleStoriesTestDouble): List<StoryEvent> = storyEventsCreated.get(double)!!

		override fun check(double: SoyleStoriesTestDouble): Boolean = get(double).size >= atLeast

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			interact {
				scope.get<CreateStoryEventController>().createStoryEvent("Story Event Name")
			}
		}
	}

	fun addedCharacter(storyEventId: StoryEvent.Id, characterId: Character.Id) = object : DependentProperty<Character> {

		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  storyEventsCreated(1)::given,
		  { it: SoyleStoriesTestDouble -> CharacterDriver.givenANumberOfCharactersHaveBeenCreated(it, 1) } as (SoyleStoriesTestDouble) -> Unit
		)

		override fun get(double: SoyleStoriesTestDouble): Character? {
			val scope = ProjectSteps.getProjectScope(double) ?: return null
			val eventRepo = scope.get<StoryEventRepository>()
			val characterRepo = scope.get<CharacterRepository>()
			val event = runBlocking { eventRepo.getStoryEventById(storyEventId) } ?: return null
			if (event.includedCharacterIds.find { it == characterId } == null) return null
			return runBlocking { characterRepo.getCharacterById(characterId) }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			interact {
				scope.get<AddCharacterToStoryEventController>().addCharacterToStoryEvent(storyEventId.uuid.toString(), characterId.uuid.toString())
			}
		}

	}

}