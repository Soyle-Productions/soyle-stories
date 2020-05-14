package com.soyle.stories.storyevent

import com.soyle.stories.DependentProperty
import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.storyevent.StoryEventsDriver.interact
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventController
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import kotlinx.coroutines.runBlocking
import org.testfx.framework.junit5.ApplicationTest

object StoryEventsDriver : ApplicationTest() {

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

		override fun get(double: SoyleStoriesTestDouble): List<StoryEvent> {
			val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
			return runBlocking {
				scope.get<StoryEventRepository>().listStoryEventsInProject(Project.Id(scope.projectId))
			}
		}

		override fun check(double: SoyleStoriesTestDouble): Boolean = get(double).size >= atLeast

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			interact {
				scope.get<CreateStoryEventController>().createStoryEvent("Story Event Name")
			}
		}
	}

}