package com.soyle.stories.usecase.framework

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SceneOrderService
import com.soyle.stories.domain.scene.sceneName
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.scene.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.usecase.scene.deleteScene.DeleteSceneUseCase
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotContain

object `Scene Steps` {

    interface Given {

        val sceneRepository: SceneRepository
        val storyEventRepository: StoryEventRepository
        val proseRepository: ProseRepository

        interface ExistenceExpectations {
            infix fun `has been created in`(project: Project.Id): Scene.Id
        }

        fun `a scene`(named: String = sceneName().value): ExistenceExpectations = object : ExistenceExpectations {
            override fun `has been created in`(project: Project.Id): Scene.Id {
                val found = runBlocking { sceneRepository.listAllScenesInProject(project) }
                    .find { it.name.value == named }
                    ?.id
                if (found == null) {
                    val deferred = CompletableDeferred<Scene.Id>()
                    val useCase = CreateNewSceneUseCase(
                        SceneOrderService(),
                        StoryEventTimeService(storyEventRepository),
                        sceneRepository,
                        proseRepository,
                        storyEventRepository
                    )
                    runBlocking {
                        useCase.invoke(CreateNewScene.RequestModel(nonBlankStr(named), project)) {
                            deferred.complete(sceneRepository.getSceneOrError(it.sceneCreated.sceneId.uuid).id)
                        }
                    }
                    return runBlocking { deferred.await() }
                } else return found
            }
        }

    }

    interface When {

        val sceneRepository: SceneRepository
        val locationRepository: LocationRepository
        val storyEventRepository: StoryEventRepository

        interface SceneActions {
            fun `is deleted`()
        }

        infix fun the(scene: Scene.Id): SceneActions = object : SceneActions {
            override fun `is deleted`() {
                runBlocking {
                    DeleteSceneUseCase(sceneRepository, locationRepository, storyEventRepository).invoke(scene) {}
                }
            }
        }

    }

    interface Then {

        val sceneRepository: SceneRepository

        interface StateAssertions {
            infix fun `should contain the`(storyEvent: StoryEvent.Id)
            infix fun `should not contain the`(storyEvent: StoryEvent.Id)
        }

        infix fun the(scene: Scene.Id): StateAssertions = object : StateAssertions {
            override fun `should contain the`(storyEvent: StoryEvent.Id) {
                runBlocking { sceneRepository.getSceneOrError(scene.uuid) }
                    .coveredStoryEvents.shouldContain(storyEvent)
            }

            override fun `should not contain the`(storyEvent: StoryEvent.Id) {
                runBlocking { sceneRepository.getSceneOrError(scene.uuid) }
                    .coveredStoryEvents.shouldNotContain(storyEvent)
            }
        }

    }

}