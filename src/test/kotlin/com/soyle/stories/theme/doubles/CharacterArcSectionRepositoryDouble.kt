package com.soyle.stories.theme.doubles

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository

class CharacterArcSectionRepositoryDouble : CharacterArcSectionRepository {

    val characterArcSections = mutableMapOf<CharacterArcSection.Id, CharacterArcSection>()

    override suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {
        characterArcSections.forEach {
            this.characterArcSections[it.id] = it
        }
    }

    override suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? =
        characterArcSections[characterArcSectionId]

    override suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> =
        characterArcSections.values.filter { it.id in characterArcSectionIds }

    override suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> =
        characterArcSections.values.filter { it.characterId == characterId }

    override suspend fun getCharacterArcSectionsForCharacterInTheme(
        characterId: Character.Id,
        themeId: Theme.Id
    ): List<CharacterArcSection> =
        characterArcSections.values.filter {
            it.characterId == characterId && it.themeId == themeId
        }

    override suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> =
        characterArcSections.values.filter { it.themeId == themeId }

    override suspend fun removeArcSections(sections: List<CharacterArcSection>) {
        sections.forEach {
            characterArcSections.remove(it.id)
        }
    }

    override suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {
        characterArcSections[characterArcSection.id] = characterArcSection
    }

}