package com.soyle.stories.entities

import com.soyle.stories.common.Entity
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
  val name: String,
  val media: Media.Id?,
  val characterArcs: List<CharacterArc>
) : Entity<Character.Id> {

	constructor(projectId: Project.Id, name: String, media: Media.Id? = null) : this(Id(), projectId, name, media, listOf())

	private fun copy(
		name: String = this.name,
		media: Media.Id? = this.media,
		characterArcs: List<CharacterArc> = this.characterArcs
	) = Character(id, projectId, name, media, characterArcs)

	fun withName(name: String): Character = copy(name = name)

	fun withCharacterArc(
		name: String,
		themeId: Theme.Id
	): Character {
		if (characterArcs.any { it.themeId == themeId }) {
			throw ValidationException("Character cannot have multiple arcs for the same theme")
		}
		val newArc = CharacterArc(id, CharacterArcTemplate.default(), themeId, name)
		return copy(characterArcs = characterArcs + newArc)
	}

	data class Id(val uuid: UUID = UUID.randomUUID())

	companion object {
		fun buildNewCharacter(projectId: Project.Id, name: String): Character = Character(projectId, name)
	}
}