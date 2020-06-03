package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.scene.CharacterNotInScene
import java.util.*

class Scene(
  override val id: Id,
  val projectId: Project.Id,
  val name: String,
  val storyEventId: StoryEvent.Id,
  private val characterMotivations: Map<Character.Id, String?>
) : Entity<Scene.Id> {

	constructor(projectId: Project.Id, name: String, storyEventId: StoryEvent.Id) : this(Id(), projectId, name, storyEventId, mapOf())

	fun includesCharacter(characterId: Character.Id): Boolean
	{
		return characterMotivations.containsKey(characterId)
	}

	fun getMotivationForCharacter(characterId: Character.Id): String?
	{
		return characterMotivations[characterId]
	}

	private fun copy(
	  name: String = this.name,
	  characterMotivations: Map<Character.Id, String?> = this.characterMotivations
	) = Scene(id, projectId, name, storyEventId, characterMotivations)

	fun withName(newName: String) = copy(name = newName)
	fun withCharacterIncluded(characterId: Character.Id) = copy(characterMotivations = characterMotivations + (characterId to null))
	fun withMotivationForCharacter(characterId: Character.Id, motivation: String?): Scene
	{
		if (! includesCharacter(characterId)) throw CharacterNotInScene(id.uuid, characterId.uuid)
		return copy(characterMotivations = characterMotivations.mapValues {
			if (it.key == characterId) motivation
			else it.value
		})
	}

	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Scene($uuid)"
	}
}