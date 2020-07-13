package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import java.util.*

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 8:19 PM
 */
class Character(
  override val id: Id,
  val projectId: Project.Id,
  // Characters have a name
  val name: String,
  val media: Media.Id? = null
) : Entity<Character.Id> {

	fun withName(name: String): Character {
		return Character(id, projectId, name)
	}

	data class Id(val uuid: UUID = UUID.randomUUID())

	companion object {
		fun buildNewCharacter(projectId: Project.Id, name: String): Character =
		  Character(Id(), projectId, name)
	}
}