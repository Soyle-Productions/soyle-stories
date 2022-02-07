package com.soyle.stories.core.definitions.storyevent

import com.soyle.stories.core.framework.storyevent.`Story Event Steps`
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.runBlocking

class `Story Event Expectations`(
    private val storyEventRepository: StoryEventRepository,

    private val `when`: `Story Event Steps`.When
) : `Story Event Steps`.Given {
    override fun `a story event`(coveredBy: Scene.Id): `Story Event Steps`.Given.ExistenceExpectations = object :
        `Story Event Steps`.Given.ExistenceExpectations {
        override fun `has been created in the`(project: Project.Id): StoryEvent.Id {
            val found = runBlocking { storyEventRepository.listStoryEventsInProject(project) }
                .find { it.sceneId == coveredBy }
                ?.id
            return found ?: error("No story event covered by $coveredBy exists in the $project")
        }
    }

    override fun `a story event`(named: String): `Story Event Steps`.Given.ExistenceExpectations =
        object : `Story Event Steps`.Given.ExistenceExpectations {
            override fun `has been created in the`(project: Project.Id): StoryEvent.Id {
                val found = runBlocking { storyEventRepository.listStoryEventsInProject(project) }
                    .find { it.name.value == named }
                    ?.id
                return found ?: `when`.`a story event`(named).`is created in the`(project)
            }
        }

    override fun the(storyEventId: StoryEvent.Id): `Story Event Steps`.Given.StateExpectations = object :
        `Story Event Steps`.Given.StateExpectations {
        override fun `has been covered by the`(sceneId: Scene.Id) {
            val storyEvent = runBlocking { storyEventRepository.getStoryEventOrError(storyEventId) }
            if (storyEvent.sceneId == sceneId) return
            `when` the storyEventId `is covered by the` sceneId
        }

        override fun `has been uncovered`() {
            val storyEvent = runBlocking { storyEventRepository.getStoryEventOrError(storyEventId) }
            if (storyEvent.sceneId == null) return
            `when`.the(storyEventId).`is uncovered`()
        }

        override fun `has been removed from the story`() {
            runBlocking { storyEventRepository.getStoryEventById(storyEventId) } ?: return
            `when`.the(storyEventId).`is removed`()
        }
    }
}