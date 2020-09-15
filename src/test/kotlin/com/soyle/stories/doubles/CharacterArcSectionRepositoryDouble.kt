package com.soyle.stories.doubles

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme

class CharacterArcSectionRepositoryDouble(
    private val onAddNewCharacterArcSections: (List<CharacterArcSection>) -> Unit = {},
    private val onUpdateCharacterArcSections: (CharacterArcSection) -> Unit = {},
    private val onRemoveCharacterArcSections: (List<CharacterArcSection>) -> Unit = {}
) {

    val characterArcSections = mutableMapOf<CharacterArcSection.Id, CharacterArcSection>()

    suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {
        onAddNewCharacterArcSections(characterArcSections)
        characterArcSections.forEach {
            this.characterArcSections[it.id] = it
        }
    }

    suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? =
        characterArcSections[characterArcSectionId]

    suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> =
        characterArcSections.values.filter { it.id in characterArcSectionIds }

    suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> =
        characterArcSections.values.filter { it.characterId == characterId }

    suspend fun getCharacterArcSectionsForCharacterInTheme(
        characterId: Character.Id,
        themeId: Theme.Id
    ): List<CharacterArcSection> =
        characterArcSections.values.filter {
            it.characterId == characterId && it.themeId == themeId
        }

    suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> =
        characterArcSections.values.filter { it.themeId == themeId }

    suspend fun removeArcSections(sections: List<CharacterArcSection>) {
        onRemoveCharacterArcSections(sections)
        sections.forEach {
            characterArcSections.remove(it.id)
        }
    }

    suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {
        onUpdateCharacterArcSections.invoke(characterArcSection)
        characterArcSections[characterArcSection.id] = characterArcSection
    }

}