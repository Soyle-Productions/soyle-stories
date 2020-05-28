package com.soyle.stories.entities

import java.util.*

class Scene(
  val id: Id,
  val projectId: Project.Id,
  val name: String,
  val storyEventId: StoryEvent.Id
) {

	private fun copy(
	  name: String = this.name
	) = Scene(id, projectId, name, storyEventId)

	fun withName(newName: String) = copy(name = newName)

	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Scene($uuid)"
	}
}