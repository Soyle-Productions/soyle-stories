package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.common.SingleNonBlankLine
import java.util.*

class Location(
	override val id: Id,
	val projectId: Project.Id,
	val name: SingleNonBlankLine,
	val description: String = ""
) : Entity<Location.Id> {

	private fun copy(
	  name: SingleNonBlankLine = this.name,
	  description: String = this.description
	) = Location(id, projectId, name, description)

	fun withName(name: SingleNonBlankLine) = copy(name = name)
	fun withDescription(description: String) = copy(description = description)

	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Location($uuid)"
	}

}

class LocationRenamed(val locationId: Location.Id, val newName: String)