package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.ValidationException
import com.soyle.stories.entities.theme.ThematicTemplate
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
  val name: NonBlankString,
  val media: Media.Id?
) : Entity<Character.Id> {

	constructor(projectId: Project.Id, name: NonBlankString, media: Media.Id? = null) : this(Id(), projectId, name, media)

	private fun copy(
		name: NonBlankString = this.name,
		media: Media.Id? = this.media
	) = Character(id, projectId, name, media)

	fun withName(name: NonBlankString): Character = copy(name = name)

	data class Id(val uuid: UUID = UUID.randomUUID())

	companion object {
		fun buildNewCharacter(projectId: Project.Id, name: NonBlankString): Character = Character(projectId, name)
	}
}