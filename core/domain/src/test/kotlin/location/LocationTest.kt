package com.soyle.stories.domain.location

import com.soyle.stories.domain.location.events.HostedSceneRenamed
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.domain.location.exceptions.HostedSceneAlreadyHasName
import com.soyle.stories.domain.location.exceptions.LocationAlreadyHostsScene
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.sceneName
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.entitySetOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class LocationTest {

	private val projectId = Project.Id(UUID.randomUUID())

	@Test
	fun locationIsEntity() {
		val id = Location.Id()
		assert(Location(id, projectId, SingleNonBlankLine.create(singleLine("Work"))!!, "", entitySetOf()).isSameEntityAs(
			Location(id, projectId, SingleNonBlankLine.create(singleLine("Home"))!!, "", entitySetOf()))
		)
	}

	@Test
	fun locationHasName() {
		val name = SingleNonBlankLine.create(singleLine("Test Name"))!!
		assertEquals(name, Location(Location.Id(), projectId, name, "", entitySetOf()).name)
	}

	@Test
	fun locationHasDescription() {
		val description = "Test Description"
		assertEquals(description, Location(Location.Id(), projectId, SingleNonBlankLine.create(singleLine("Name"))!!, description, entitySetOf()).description)
	}

	@Test
	fun descriptionIsNotRequired() {
		Location(Location.Id(), projectId, SingleNonBlankLine.create(singleLine("Name"))!!, hostedScenes = entitySetOf())
	}

	@Nested
	inner class `Hosted Scenes`
	{
		private val scene = makeScene()

		@Test
		fun `can host a scene`() {
			val initialLocation = makeLocation()

			val (location, hostedScene) = initialLocation.withSceneHosted(scene.id, scene.name.value) as Updated

			hostedScene.mustEqual(SceneHostedAtLocation(initialLocation.id, scene.id, scene.name.value))

			location.hostedScenes.containsEntityWithId(scene.id).mustEqual(true)
			location.hostedScenes.single().sceneName.mustEqual(scene.name.value)
		}

		@Test
		fun `cannot host the same scene more than once`() {
			val (initialLocation) = makeLocation().withSceneHosted(scene.id, scene.name.value)

			val (location, failure) = initialLocation.withSceneHosted(scene.id, scene.name.value) as NoUpdate

			failure.mustEqual(LocationAlreadyHostsScene(initialLocation.id, scene.id))

			location.hostedScenes.containsEntityWithId(scene.id).mustEqual(true)
			location.hostedScenes.single().sceneName.mustEqual(scene.name.value)
		}

		@Test
		fun `can rename hosted scene`() {
			val newSceneName = sceneName()
			val (initialLocation) = makeLocation().withSceneHosted(scene.id, scene.name.value)
			val (location, hostedSceneRenamed) = initialLocation
                .withHostedScene(scene.id)!!.renamed(to = newSceneName.value) as Updated

            hostedSceneRenamed.mustEqual(HostedSceneRenamed(initialLocation.id, scene.id, newSceneName.value))
            location.hostedScenes.single().sceneName.mustEqual(newSceneName.value)
		}

        @Test
        fun `cannot modify hosted scene that does exist`() {
            assertNull(makeLocation().withHostedScene(scene.id))
        }

        @Test
        fun `renaming hosted scenes to same name should result in no update`() {
            val (initialLocation) = makeLocation().withSceneHosted(scene.id, scene.name.value)
            val (location, failure) = initialLocation
                .withHostedScene(scene.id)!!.renamed(to = scene.name.value) as NoUpdate
            failure.mustEqual(HostedSceneAlreadyHasName(location.id, scene.id, scene.name.value))
        }

	}
}