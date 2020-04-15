package com.soyle.stories.location

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class LocationTest {

	private val projectId = Project.Id(UUID.randomUUID())

	@Test
	fun locationIsEntity() {
		val id = Location.Id()
		assert(Location(id, projectId, "", "") isSameEntityAs Location(id, projectId, "Bob", ""))
	}

	@Test
	fun locationHasName() {
		val name = "Test Name"
		assertEquals(name, Location(Location.Id(), projectId, name, "").name)
	}

	@Test
	fun locationHasDescription() {
		val description = "Test Description"
		assertEquals(description, Location(Location.Id(), projectId, "", description).description)
	}

	@Test
	fun descriptionIsNotRequired() {
		Location(Location.Id(), projectId, "")
	}
}