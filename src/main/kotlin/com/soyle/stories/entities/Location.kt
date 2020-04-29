package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import java.util.*

class Location(
  override val id: Id,
  val projectId: Project.Id,
  val name: String,
  val description: String = ""
) : Entity<Location.Id> {

	private fun copy(
	  name: String = this.name,
	  description: String = this.description
	) = Location(id, projectId, name, description)

	fun withName(name: String) = copy(name = name)
	fun withDescription(description: String) = copy(description = description)

	data class Id(val uuid: UUID = UUID.randomUUID())

}