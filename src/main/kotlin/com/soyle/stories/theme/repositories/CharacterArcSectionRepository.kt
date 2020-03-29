/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 9:12 PM
 */
package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme

interface CharacterArcSectionRepository {
    suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection?
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