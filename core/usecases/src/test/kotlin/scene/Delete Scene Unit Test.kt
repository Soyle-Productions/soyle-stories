package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import com.soyle.stories.usecase.scene.deleteScene.DeleteSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Delete Scene Unit Test` {

    // Preconditions
    /** A project must have been started */
    val projectId = Project.Id()

    /** The scene must exist */
    private val scene = makeScene(projectId = projectId, coveredStoryEvents = emptySet())

    // Post conditions
    /** The scene must be deleted from the repository */
    private var deletedScene: Scene.Id? = null
    /** Scene order must be updated */
    private var updatedSceneOrder: SceneOrder? = null
    /** Locations hosting the scene must be updated */
    private val updatedLocations: List<Location>
    /** Covered story event must be uncovered (if any exist) */
    private var updatedStoryEvent: StoryEvent? = null

    // output
    private var result: DeleteScene.ResponseModel? = null

    // repositories
    private val sceneRepository = SceneRepositoryDouble(
        onRemoveScene = ::deletedScene::set,
        onUpdateSceneOrder = ::updatedSceneOrder::set
    )
    private val locationRepository: LocationRepositoryDouble
    private val storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = ::updatedStoryEvent::set)

    init {
        val locationsList = mutableListOf<Location>()
        locationRepository = LocationRepositoryDouble(onUpdateLocation = locationsList::add)
        updatedLocations = locationsList
    }

    // use case
    val useCase: DeleteScene = DeleteSceneUseCase(sceneRepository, locationRepository, storyEventRepository)
    private fun deleteScene() = runBlocking {
        useCase.invoke(scene.id) {
            result = it
        }
    }

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> { deleteScene() }

        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should remove scene from repository`() {
            deleteScene()

            val deletedScene = deletedScene!!
            deletedScene.uuid.mustEqual(scene.id.uuid)
            result!!.sceneRemoved.sceneId.mustEqual(scene.id)
        }

        @Test
        fun `should update scene order`() {
            deleteScene()

            updatedSceneOrder!!.projectId.mustEqual(projectId)
            updatedSceneOrder!!.order.contains(scene.id).mustEqual(false)
        }

        @Test
        fun `should output scene removed event`() {
            deleteScene()

            result!!.sceneRemoved
        }

        @Test
        fun `when other scenes exist, should only remove this scene from scene order`() {
            repeat(5) { sceneRepository.givenScene(makeScene(projectId = projectId)) }

            deleteScene()

            updatedSceneOrder!!.order.size.mustEqual(5)
        }

        @Test
        fun `should not update any locations`() {
            deleteScene()

            assertTrue(updatedLocations.isEmpty())
        }

        @Test
        fun `should not update any story events`() {
            deleteScene()

            updatedStoryEvent.shouldBeNull()
            result!!.storyEventUncovered.shouldBeNull()
        }

        @Nested
        inner class `Given Locations are Linked` {

            private val unlinkedLocations = List(3) { makeLocation(projectId = projectId) }
            private val locations =
                List(5) { makeLocation(projectId = projectId).withSceneHosted(scene.id, scene.name.value).location }

            init {
                unlinkedLocations.forEach(locationRepository::givenLocation)
                locations.forEach(locationRepository::givenLocation)
                locations.fold(scene) { nextScene, location -> nextScene.withLocationLinked(location).scene }
                    .let(sceneRepository::givenScene)
            }

            @Test
            fun `should update locations to remove hosted scene`() {
                deleteScene()

                updatedLocations.map { it.id }.toSet().mustEqual(locations.map { it.id }.toSet())
                updatedLocations.forEach {
                    it.hostedScenes.containsEntityWithId(scene.id).mustEqual(false)
                }
            }

            @Test
            fun `should output hosted scenes removed events`() {
                deleteScene()

                with(result!!) {
                    hostedScenesRemoved.map { it.locationId }.toSet().mustEqual(locations.map { it.id }.toSet())
                    hostedScenesRemoved.forEach { it.sceneId.mustEqual(scene.id) }
                }
            }

        }

        @Nested
        inner class `Given Scene Covers a Story Event` {

            private val storyEvent = makeStoryEvent(sceneId = scene.id)

            init {
                storyEventRepository.givenStoryEvent(storyEvent)
                sceneRepository.givenScene(scene.withStoryEvent(storyEvent).scene)
            }

            @Test
            fun `should update story event to not be covered`() {
                deleteScene()

                updatedStoryEvent.shouldNotBeNull().run {
                    id.mustEqual(storyEvent.id) { "Wrong story event was updated" }
                    sceneId.shouldBeNull()
                }
            }

            @Test
            fun `should produce story event uncovered event`() {
                deleteScene()

                with(result!!) {
                    storyEventUncovered.shouldNotBeNull()
                        .shouldBeEqualTo(StoryEventUncoveredFromScene(storyEvent.id, scene.id))
                }
            }

        }

    }

}
