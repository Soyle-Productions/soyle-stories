package com.soyle.stories.domain.location

import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.entitySetOf
import org.junit.jupiter.api.Assertions.assertEquals
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

	}
}