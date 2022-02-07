package com.soyle.stories.core.definitions.storyevent

import com.soyle.stories.core.framework.storyevent.`Story Event Steps`
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInSceneUseCase
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromSceneUseCase
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProjectUseCase
import kotlinx.coroutines.runBlocking
import java.util.logging.Logger

class `Story Event Commands`(
    private val storyEventRepository: StoryEventRepository,
    private val sceneRepository: SceneRepository
) : `Story Event Steps`.When {
    override fun `a story event`(
        named: String,
        atTime: Long,
        coveredBy: Scene.Id?
    ): `Story Event Steps`.When.CreationActions = object :
        `Story Event Steps`.When.CreationActions {
        override fun `is created in the`(project: Project.Id): StoryEvent.Id {
            val useCase = CreateStoryEventUseCase(storyEventRepository, sceneRepository)
            lateinit var storyEvent: StoryEvent.Id
            val request = CreateStoryEvent.RequestModel(
                nonBlankStr(named),
                project,
                atTime,
                coveredBy
            )
            runBlocking {
                useCase(request) {
                    storyEvent = it.createdStoryEvent.storyEventId
                }
            }
            return storyEvent
        }
    }

    override fun the(storyEvent: StoryEvent.Id): `Story Event Steps`.When.StoryEventActions = object :
        `Story Event Steps`.When.StoryEventActions {
        override fun `is covered by the`(scene: Scene.Id) {
            val logger = Logger.getGlobal()
            val useCase = CoverStoryEventInSceneUseCase(
                storyEventRepository,
                sceneRepository,
            )
            runBlocking {
                useCase(storyEvent, scene) {
                    logger.info(it.toString())
                }.exceptionOrNull()?.also { throw it }
            }
        }

        override fun `is uncovered`() {
            runBlocking {
                UncoverStoryEventFromSceneUseCase(storyEventRepository).invoke(storyEvent) {}
            }
        }

        override fun `is removed`() {
            runBlocking {
                RemoveStoryEventFromProjectUseCase(storyEventRepository).invoke(storyEvent) {}
            }
        }
    }
}