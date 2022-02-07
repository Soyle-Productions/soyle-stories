package com.soyle.stories.usecase.storyevent.coverage

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyCoveredByScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInScene
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class `Cover Story Event in Scene Unit Test` {

    // Pre-requisites
    /** Project must be started */
    private val projectId = Project.Id()

    /** Scene must exist */
    private val scene = makeScene(projectId = projectId)

    /** Story Event must exist */
    private val storyEvent = makeStoryEvent(projectId = projectId)

    // Post Conditions
    /** Should update the story event */
    private var updatedStoryEvent: StoryEvent? = null

    /** Should output coverage event */
    private var event: StoryEventCoveredByScene? = null

    // Repositories
    private val storyEventRepository: StoryEventRepositoryDouble = StoryEventRepositoryDouble(onUpdateStoryEvent = ::updatedStoryEvent::set)
    private val sceneRepository: SceneRepositoryDouble = SceneRepositoryDouble()

    // other services

    // Use Case
    private val useCase: CoverStoryEventInScene = CoverStoryEventInSceneUseCase(
        storyEventRepository,
        sceneRepository
    )
    private fun coverStoryEventInScene(): Result<Nothing?> {
        return runBlocking {
            useCase.invoke(storyEvent.id, scene.id) {
                event = it
            }
        }
    }

    @TestFactory
    fun `Story Event Doesn't Exist`(): List<DynamicTest> {
        sceneRepository.givenScene(scene)

        val error = coverStoryEventInScene().exceptionOrNull() as StoryEventDoesNotExist

        return shouldFailCompletely {
            error.storyEventId shouldBeEqualTo storyEvent.id.uuid
        }
    }

    @TestFactory
    fun `Scene Doesn't Exist`(): List<DynamicTest> {
        storyEventRepository.givenStoryEvent(storyEvent)

        val error = coverStoryEventInScene().exceptionOrNull() as SceneDoesNotExist

        return shouldFailCompletely {
            error.sceneId shouldBeEqualTo scene.id.uuid
        }
    }

    @TestFactory
    fun `Story Event Already Covered by the Scene`(): List<DynamicTest> {
        storyEventRepository.givenStoryEvent(storyEvent.coveredByScene(scene.id).storyEvent)
        sceneRepository.givenScene(scene)

        val error = coverStoryEventInScene().exceptionOrNull()!!

        return shouldFailCompletely {
            error.shouldBeEqualTo(StoryEventAlreadyCoveredByScene(storyEvent.id, scene.id))
        }
    }

    @TestFactory
    fun `Story Event Not Yet Covered by the Scene`(): List<DynamicTest> {
        storyEventRepository.givenStoryEvent(storyEvent)
        sceneRepository.givenScene(scene)

        coverStoryEventInScene().exceptionOrNull().shouldBeNull()

        return listOf(
            dynamicTest("should update story event") {
                updatedStoryEvent!!.id.shouldBeEqualTo(storyEvent.id)
                updatedStoryEvent!!.sceneId.shouldBeEqualTo(scene.id)
            },
            dynamicTest("should produce story event covered by scene event") {
                with(event!!) {
                    storyEventId.mustEqual(storyEvent.id)
                    sceneId.mustEqual(scene.id)
                }
            },
            dynamicTest("should not output a previous scene id") {
                event!!.uncovered.shouldBeNull()
            }
        )
    }

    @TestFactory
    fun `Another Scene Currently Covers Story Event`(): List<DynamicTest> {
        val otherScene = makeScene()
        storyEventRepository.givenStoryEvent(storyEvent.coveredByScene(otherScene.id).storyEvent)
        sceneRepository.givenScene(otherScene)
        sceneRepository.givenScene(scene)

        coverStoryEventInScene().exceptionOrNull().shouldBeNull()

        return listOf(
            dynamicTest("should update story event") {
                updatedStoryEvent!!.id.shouldBeEqualTo(storyEvent.id)
                updatedStoryEvent!!.sceneId.shouldBeEqualTo(scene.id)
            },
            dynamicTest("should produce story event covered by scene event") {
                with(event!!) {
                    storyEventId.mustEqual(storyEvent.id)
                    sceneId.mustEqual(scene.id)
                }
            },
            dynamicTest("should output previous scene id") {
                event!!.uncovered shouldBeEqualTo StoryEventUncoveredFromScene(storyEvent.id, otherScene.id)
            }
        )
    }


    private fun shouldFailCompletely(name: String = "Should throw error", testError: () -> Unit): List<DynamicTest> {
        return listOf(
            dynamicTest(name, testError),
            dynamicTest("should not update story event") {
                updatedStoryEvent.shouldBeNull()
            },
            dynamicTest("should not produce event") {
                event.shouldBeNull()
            }
        )
    }
}