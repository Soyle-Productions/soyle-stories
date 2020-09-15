/**
 * Created by Brendan
 * Date: 3/4/2020
 * Time: 11:26 PM
 */
package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.*

interface CharacterArcRepository {
    suspend fun getCharacterArcByCharacterAndThemeId(characterId: Character.Id, themeId: Theme.Id): CharacterArc?
    suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc>
    suspend fun addNewCharacterArc(characterArc: CharacterArc)
    suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id)
    suspend fun listCharacterArcsForCharacter(characterId: Character.Id): List<CharacterArc>
    suspend fun replaceCharacterArcs(vararg characterArcs: CharacterArc)
    suspend fun removeCharacterArcs(vararg characterArcs: CharacterArc)
    suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc>
    suspend fun listAllCharacterArcsInTheme(themeId: Theme.Id): List<CharacterArc>


    suspend fun getCharacterArcContainingArcSection(characterArcSectionId: CharacterArcSection.Id): CharacterArc?
    suspend fun getCharacterArcsContainingArcSections(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArc>
}