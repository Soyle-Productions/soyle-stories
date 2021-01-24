package com.soyle.stories.repositories

import com.soyle.stories.entities.*
import com.soyle.stories.location.repositories.CharacterArcSectionRepository

class CharacterArcRepositoryImpl : com.soyle.stories.theme.repositories.CharacterArcRepository, CharacterArcSectionRepository {

	val characterArcs = mutableMapOf<Pair<Character.Id, Theme.Id>, CharacterArc>()
	val characterArcsBySections = mutableMapOf<CharacterArcSection.Id, Pair<Character.Id, Theme.Id>>()

	@Synchronized
	override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
		val arcKey = characterArc.characterId to characterArc.themeId
		characterArcs[arcKey] = characterArc
		characterArc.arcSections.forEach {
			characterArcsBySections[it.id] = arcKey
		}
	}

	@Synchronized
	override suspend fun getCharacterArcByCharacterAndThemeId(
	  characterId: Character.Id,
	  themeId: Theme.Id
	): CharacterArc? = characterArcs[characterId to themeId]

	@Synchronized
	override suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> = characterArcs.values.filter { it.themeId == themeId }

	@Synchronized
	override suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
		characterArcs.remove(characterId to themeId)?.let { arc ->
			arc.arcSections.forEach {
				characterArcsBySections.remove(it.id)
			}
		}
	}

	@Synchronized
	override suspend fun getCharacterArcContainingArcSection(characterArcSectionId: CharacterArcSection.Id): CharacterArc? {
		return characterArcsBySections[characterArcSectionId]
			?.let(characterArcs::get)
	}

	@Synchronized
	override suspend fun getCharacterArcsContainingArcSections(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArc> {
		return characterArcSectionIds.mapNotNull {
			getCharacterArcContainingArcSection(it)
		}.toSet().toList()
	}

	@Synchronized
	override suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc> {
		return characterArcs.values.toList()
	}

	@Synchronized
	override suspend fun listAllCharacterArcsInTheme(themeId: Theme.Id): List<CharacterArc> {
		return characterArcs.values.filter { it.themeId == themeId }
	}

	@Synchronized
	override suspend fun listCharacterArcsForCharacter(characterId: Character.Id): List<CharacterArc> {
		return characterArcs.values.filter { it.characterId == characterId }
	}

	@Synchronized
	override suspend fun removeCharacterArcs(vararg characterArcs: CharacterArc) {
		characterArcs.forEach { removeCharacterArc(it.themeId, it.characterId) }
	}

	@Synchronized
	override suspend fun replaceCharacterArcs(vararg characterArcs: CharacterArc) {
		removeCharacterArcs(*characterArcs)
		characterArcs.forEach {
			addNewCharacterArc(it)
		}
	}

	@Synchronized
	override fun getCharacterArcSectionsLinkedToLocation(locationId: Location.Id): List<CharacterArcSection> = emptyList()

	@Synchronized
	override fun updateCharacterArcSections(characterArcSections: Set<CharacterArcSection>) {
	}
}