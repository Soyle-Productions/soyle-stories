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
  private val charactersInScene: List<CharacterInScene>
) : Entity<Scene.Id> {

	constructor(projectId: Project.Id, name: String, storyEventId: StoryEvent.Id) : this(Id(), projectId, name, storyEventId, null, listOf())

	private val charactersById by lazy { charactersInScene.associateBy { it.characterId } }

	fun includesCharacter(characterId: Character.Id): Boolean
	{
		return charactersById.containsKey(characterId)
	}

	fun getMotivationForCharacter(characterId: Character.Id): CharacterMotivation?
	{
		return charactersById[characterId]?.let {
			CharacterMotivation(it.characterId, it.characterName, it.motivation)
		}
	}

	fun getCoveredCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection.Id>?
	{
		return charactersById[characterId]?.coveredArcSections
	}

	private val allCharacterArcSections by lazy {
		charactersById.values.flatMap { it.coveredArcSections }.toSet()
	}
	fun isCharacterArcSectionCovered(characterArcSectionId: CharacterArcSection.Id): Boolean
	{
		return allCharacterArcSections.contains(characterArcSectionId)
	}

	val includedCharacters: List<IncludedCharacter> by lazy {
		charactersInScene.map { IncludedCharacter(it.characterId, it.characterName) }
	}

	fun hasCharacters(): Boolean = charactersInScene.isNotEmpty()

	private fun copy(
	  name: String = this.name,
	  locationId: Location.Id? = this.locationId,
	  charactersInScene: List<CharacterInScene> = this.charactersInScene
	) = Scene(id, projectId, name, storyEventId, locationId, charactersInScene)

	fun withName(newName: String) = copy(name = newName)
	fun withCharacterIncluded(character: Character) = copy(charactersInScene = charactersInScene + CharacterInScene(character.id, id, character.name, null, listOf()))
	fun withMotivationForCharacter(characterId: Character.Id, motivation: String?): Scene
	{
		if (! includesCharacter(characterId)) throw CharacterNotInScene(id.uuid, characterId.uuid)
		return copy(charactersInScene = charactersInScene.map {
			if (it.characterId == characterId) CharacterInScene(it.characterId, id, it.characterName, motivation, listOf())
			else it
		})
	}
	fun withLocationLinked(locationId: Location.Id) = copy(locationId = locationId)
	fun withoutLocation() = copy(locationId = null)
	fun withoutCharacter(characterId: Character.Id) = copy(charactersInScene = charactersInScene.filterNot { it.characterId == characterId })

	fun withCharacterArcSectionCovered(characterId: Character.Id, characterArcSection: CharacterArcSection): Scene
	{
		charactersById[characterId] ?: throw CharacterNotInScene(id.uuid, characterId.uuid)
		return copy(
			charactersInScene = charactersInScene.map {
				if (it.characterId != characterId) it
				else it.withCoveredArcSection(characterArcSection)
			}
		)
	}

	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Scene($uuid)"
	}

	class CharacterMotivation(val characterId: Character.Id, val characterName: String, val motivation: String?) {
		fun isInherited() = motivation == null
	}
	class IncludedCharacter(val characterId: Character.Id, val characterName: String)
}