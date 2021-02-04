package com.soyle.stories.entities

import java.util.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 2:39 PM
 */
class CharacterArcSection(
  val id: Id,
  val characterId: Character.Id,
  val themeId: Theme.Id,
  val template: CharacterArcTemplateSection,
  val linkedLocation: Location.Id?,
  val value: String
) {

	private fun copy(
	  linkedLocation: Location.Id? = this.linkedLocation,
	  value: String = this.value
	) = CharacterArcSection(id, characterId, themeId, template, linkedLocation, value)

	fun withValue(value: String) = copy(value = value)
	fun withLinkedLocation(locationId: Location.Id) = copy(linkedLocation = locationId)
	fun withoutLinkedLocation() = copy(linkedLocation = null)

	data class Id(val uuid: UUID)

	companion object {
		fun planNewCharacterArcSection(
		  characterId: Character.Id,
		  themeId: Theme.Id,
		  template: CharacterArcTemplateSection
		) =
		  CharacterArcSection(
			Id(
			  UUID.randomUUID()
			), characterId, themeId, template, null, ""
		  )
	}

}