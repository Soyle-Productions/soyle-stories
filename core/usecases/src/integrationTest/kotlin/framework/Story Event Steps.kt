package com.soyle.stories.usecase.framework

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.storyEventName
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInSceneUseCase
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromSceneUseCase
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProjectUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.fail
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo

object `Story Event Steps` {

    interface Given {

        val storyEventRepository: StoryEventRepository

        interface ExistenceExpectations {
            infix fun `has been created in`(project: Project.Id): StoryEvent.Id
        }
        fun `a story event`(named: String = storyEventName().value): ExistenceExpectations = object : ExistenceExpectations {
            override fun `has been created in`(project: Project.Id): StoryEvent.Id {
                val found = runBlocking { storyEventRepository.listStoryEventsInProject(project) }
                    .find { it.name.value == named }
                    ?.id
                if (found == null) {
                    val deferred = CompletableDeferred<StoryEvent.Id>()
                    val useCase = CreateStoryEventUseCase(storyEventRepository)
                    runBlocking {
                        useCase(CreateStoryEvent.RequestModel(nonBlankStr(named), project, null)) {
                            deferred.complete(storyEventRepository.getStoryEventOrError(it.createdStoryEvent.storyEventId).id)
                        }
                    }
                    return runBlocking { deferred.await() }
                } else return found
            }
        }
    }

    interface When {

        val storyEventRepository: StoryEventRepository
        val sceneRepository: SceneRepository

        interface StoryEventActions {
            infix fun `is covered by the`(scene: Scene.Id)
            fun `is uncovered`()
            fun `is deleted`()
        }
        infix fun the(storyEvent: StoryEvent.Id): StoryEventActions = object : StoryEventActions {
            override fun `is covered by the`(scene: Scene.Id) {
                runBlocking {
                    CoverStoryEventInSceneUseCase(storyEventRepository, sceneRepository).invoke(storyEvent, scene) {}
                }
            }

            override fun `is uncovered`() {
                runBlocking {
                    UncoverStoryEventFromSceneUseCase(storyEventRepository, sceneRepository).invoke(storyEvent) { _ , _ -> }
                }
            }

            override fun `is deleted`() {
                runBlocking {
                    RemoveStoryEventFromProjectUseCase(storyEventRepository, sceneRepository).invoke(storyEvent) {}
                }
            }
        }

    }

    interface Then {

        val storyEventRepository: StoryEventRepository

        interface ExistenceAssertions {

            fun `should have been created in`(project: Project.Id): StoryEvent.Id
            fun `should not exist`(inProject: Project.Id)
        }

        fun `a story event`(named: String): ExistenceAssertions = object : ExistenceAssertions {
            override fun `should have been created in`(project: Project.Id): StoryEvent.Id {
                return runBlocking { storyEventRepository.listStoryEventsInProject(project) }
                    .first { it.name.value == named }
                    .id
            }

            override fun `should not exist`(inProject: Project.Id) {
                runBlocking { storyEventRepository.listStoryEventsInProject(inProject) }
                    .find { it.name.value == named }
                    ?.let { fail("Story event with name $named should not exist in $inProject, but found $it") }
            }
        }

        interface StateAssertions {
            infix fun `should be covered by the`(scene: Scene.Id)
            infix fun `should not be covered by the`(scene: Scene.Id)
        }
        infix fun the(storyEvent: StoryEvent.Id): StateAssertions = object : StateAssertions {
            override fun `should be covered by the`(scene: Scene.Id) {
                runBlocking { storyEventRepository.getStoryEventOrError(storyEvent) }
                    .sceneId.shouldBeEqualTo(scene)
            }
            override fun `should not be covered by the`(scene: Scene.Id) {
                runBlocking { storyEventRepository.getStoryEventOrError(storyEvent) }
                    .sceneId.shouldNotBeEqualTo(scene)
            }
        }

    }

}