package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.CreateStoryEventPromptView
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.runBlocking

class `Story Event Robot` private constructor(private val projectScope: ProjectScope){

    fun givenStoryEventExists(withName: NonBlankString, atTime: Int? = null): StoryEvent {
        val existingStoryEvent = getStoryEventByName(withName.value)
        if (existingStoryEvent == null) {
            createStoryEvent(withName, atTime)
            return getStoryEventByName(withName.value)!!
        }
        return existingStoryEvent
    }

    fun getStoryEventByName(name: String): StoryEvent? {
        val storyEventRepository = projectScope.get<StoryEventRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allStoryEvents = runBlocking { storyEventRepository.listStoryEventsInProject(projectId) }
        return allStoryEvents.find { it.name.value == name }
    }

    private fun createStoryEvent(name: NonBlankString, time: Int?) {
        val controller = projectScope.get<CreateStoryEventController>()
        controller.create()
        awaitWithTimeout(100) {
            robot.getOpenDialog<CreateStoryEventPromptView>() != null
        }
        robot.getOpenDialog<CreateStoryEventPromptView>()!!.createStoryEventNamed(name.value, time)
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { `Story Event Robot`(this) } }
        }

        operator fun invoke(workbench: WorkBench): `Story Event Robot` = workbench.scope.get()
    }

}