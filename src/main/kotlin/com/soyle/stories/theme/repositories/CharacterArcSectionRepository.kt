package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme

@Deprecated(message = "Character Arcs contain arc sections.", replaceWith = ReplaceWith("CharacterArcRepository"))
interface CharacterArcSectionRepository {

    @Deprecated(
        message = "Character Arcs contain arc sections.",
        replaceWith = ReplaceWith("CharacterArcRepository.getCharacterArcContainingArcSection(characterArcSectionId)")
    )
    suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection?

    @Deprecated(
        message = "Character Arcs contain arc sections.",
        replaceWith = ReplaceWith("CharacterArcRepository.getCharacterArcsContainingArcSections(characterArcSectionIds)")
    )
    suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection>

    suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection)
    suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>)
    suspend fun removeArcSections(sections: List<CharacterArcSection>)
    suspend fun getCharacterArcSectionsForCharacterInTheme(
        characterId: Character.Id,
        themeId: Theme.Id
    ): List<CharacterArcSection>

    suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection>
    suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection>
}