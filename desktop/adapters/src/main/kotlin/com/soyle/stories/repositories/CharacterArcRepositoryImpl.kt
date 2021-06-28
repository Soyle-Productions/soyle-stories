package com.soyle.stories.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcRepository

class CharacterArcRepositoryImpl : CharacterArcRepository {

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
	override suspend fun getCharacterArcContainingArcSection(arcSectionId: CharacterArcSection.Id): CharacterArc? {
		return characterArcsBySections[arcSectionId]
			?.let(characterArcs::get)
	}

	@Synchronized
	override suspend fun getCharacterArcsContainingArcSections(arcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArc> {
		return arcSectionIds.mapNotNull {
			getCharacterArcContainingArcSection(it)
		}.toSet().toList()
	}

	@Synchronized
	override suspend fun getCharacterArcsWithSectionsLinkedToLocation(locationId: Location.Id): List<CharacterArc> {
		return characterArcs.values.filter { arc -> arc.arcSections.any { it.linkedLocation == locationId } }
	}

	@Synchronized
	override suspend fun updateCharacterArcs(characterArcs: Set<CharacterArc>) {
		characterArcs.forEach {
			removeCharacterArc(it.themeId, it.characterId)
			addNewCharacterArc(it)
		}
	}
	@Synchronized
	private fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> = characterArcs.values.filter { it.themeId == themeId }

	@Synchronized
	private fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
		characterArcs.remove(characterId to themeId)?.let { arc ->
			arc.arcSections.forEach {
				characterArcsBySections.remove(it.id)
			}
		}
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
}