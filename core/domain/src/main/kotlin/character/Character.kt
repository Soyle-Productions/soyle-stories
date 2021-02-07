package com.soyle.stories.domain.character

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.media.Media
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class Character(
	override val id: Id,
	val projectId: Project.Id,
	val name: NonBlankString,
	val media: Media.Id?
) : Entity<Character.Id> {

	constructor(projectId: Project.Id, name: NonBlankString, media: Media.Id? = null) : this(Id(), projectId, name, media)

	private fun copy(
		name: NonBlankString = this.name,
		media: Media.Id? = this.media
	) = Character(id, projectId, name, media)

	fun withName(name: NonBlankString): Character = copy(name = name)

	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Character($uuid)"
	}

	companion object {
		fun buildNewCharacter(projectId: Project.Id, name: NonBlankString): Character = Character(projectId, name)
	}
}

class CharacterRenamed(val characterId: Character.Id, val newName: String)