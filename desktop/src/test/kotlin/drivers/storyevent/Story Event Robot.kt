package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.scene.SceneDriver
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.setting.SceneSettingToolRoot.Styles.Companion.sceneName
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import kotlinx.coroutines.runBlocking
import java.util.*

class `Story Event Robot` private constructor(private val projectScope: ProjectScope){

    fun givenStoryEventExists(withName: NonBlankString, atTime: Int): StoryEvent {
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
        val storyEvent = allStoryEvents.find { it.name == name }
        return storyEvent
    }

    private fun createStoryEvent(name: NonBlankString, time: Int) {
        val controller = projectScope.get<CreateStoryEventController>()
        runBlocking {
            controller.createStoryEvent(name).join()
        }
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { `Story Event Robot`(this) } }
        }

        operator fun invoke(workbench: WorkBench): `Story Event Robot` = workbench.scope.get()
    }

}