package com.soyle.stories.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository

class CharacterArcSectionRepositoryImpl : CharacterArcSectionRepository, com.soyle.stories.location.repositories.CharacterArcSectionRepository {
	val arcSections = mutableMapOf<CharacterArcSection.Id, CharacterArcSection>()
	override suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? {
		return arcSections[characterArcSectionId]
	}

	override suspend fun removeArcSections(sections: List<CharacterArcSection>) {
		sections.forEach {
			arcSections.remove(it.id)
		}
	}

	override suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {
		arcSections[characterArcSection.id] = characterArcSection
	}

	override suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {
		arcSections.putAll(characterArcSections.map { it.id to it })
	}

	override suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> {
		return arcSections.filterValues { it.characterId == characterId }.values.toList()
	}

	override suspend fun getCharacterArcSectionsForCharacterInTheme(
	  characterId: Character.Id,
	  themeId: Theme.Id
	): List<CharacterArcSection> {
		return arcSections.filterValues { it.themeId == themeId && it.characterId == characterId }.values.toList()
	}

	override suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> {
		return arcSections.filterKeys { it in characterArcSectionIds }.values.toList()
	}

	override suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> {
		return arcSections.filterValues { it.themeId == themeId }.values.toList()
	}

	override fun getCharacterArcSectionsLinkedToLocation(locationId: Location.Id): List<CharacterArcSection> {
		return arcSections.values.filter { it.linkedLocation == locationId }
	}

	override fun updateCharacterArcSections(characterArcSections: Set<CharacterArcSection>) {
		arcSections.putAll(characterArcSections.map { it.id to it })
	}
}