package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.scene.CharacterNotInScene
import java.util.*

class Scene(
  override val id: Id,
  val projectId: Project.Id,
  val name: String,
  val storyEventId: StoryEvent.Id,
  val locationId: Location.Id?,
  val characterMotivations: List<CharacterMotivation>
) : Entity<Scene.Id> {

	constructor(projectId: Project.Id, name: String, storyEventId: StoryEvent.Id) : this(Id(), projectId, name, storyEventId, null, listOf())

	private val motivationsById by lazy { characterMotivations.associateBy { it.characterId } }

	fun includesCharacter(characterId: Character.Id): Boolean
	{
		return motivationsById.containsKey(characterId)
	}

	fun getMotivationForCharacter(characterId: Character.Id): CharacterMotivation?
	{
		return motivationsById[characterId]
	}

	fun hasCharacters(): Boolean = characterMotivations.isNotEmpty()

	private fun copy(
	  name: String = this.name,
	  locationId: Location.Id? = this.locationId,
	  characterMotivations: List<CharacterMotivation> = this.characterMotivations
	) = Scene(id, projectId, name, storyEventId, locationId, characterMotivations)

	fun withName(newName: String) = copy(name = newName)
	fun withCharacterIncluded(character: Character) = copy(characterMotivations = characterMotivations + CharacterMotivation(character.id, character.name, null))
	fun withMotivationForCharacter(characterId: Character.Id, motivation: String?): Scene
	{
		if (! includesCharacter(characterId)) throw CharacterNotInScene(id.uuid, characterId.uuid)
		return copy(characterMotivations = characterMotivations.map {
			if (it.characterId == characterId) CharacterMotivation(it.characterId, it.characterName, motivation)
			else it
		})
	}
	fun withLocationLinked(locationId: Location.Id) = copy(locationId = locationId)
	fun withoutLocation() = copy(locationId = null)

	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Scene($uuid)"
	}

	class CharacterMotivation(val characterId: Character.Id, val characterName: String, val motivation: String?) {
		fun isInherited() = motivation == null
	}
}