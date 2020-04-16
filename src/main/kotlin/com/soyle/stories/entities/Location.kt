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
	  name: String = this.name
	) = Location(id, projectId, name, description)

	fun withName(name: String) = copy(name = name)

	data class Id(val uuid: UUID = UUID.randomUUID())

}