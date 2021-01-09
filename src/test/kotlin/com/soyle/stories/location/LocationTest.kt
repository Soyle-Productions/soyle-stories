package com.soyle.stories.location

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.common.singleLine
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
		assert(Location(id, projectId, SingleNonBlankLine.create(singleLine("Work"))!!, "").isSameEntityAs(
			Location(id, projectId, SingleNonBlankLine.create(singleLine("Home"))!!, ""))
		)
	}

	@Test
	fun locationHasName() {
		val name = SingleNonBlankLine.create(singleLine("Test Name"))!!
		assertEquals(name, Location(Location.Id(), projectId, name, "").name)
	}

	@Test
	fun locationHasDescription() {
		val description = "Test Description"
		assertEquals(description, Location(Location.Id(), projectId, SingleNonBlankLine.create(singleLine("Name"))!!, description).description)
	}

	@Test
	fun descriptionIsNotRequired() {
		Location(Location.Id(), projectId, SingleNonBlankLine.create(singleLine("Name"))!!)
	}
}