package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import java.util.*

interface CharacterArcRepository {
    suspend fun addNewCharacterArc(characterArc: CharacterArc)
    suspend fun getCharacterArcByCharacterAndThemeId(characterId: Character.Id, themeId: Theme.Id): CharacterArc?
    suspend fun getCharacterArcOrError(characterId: UUID, themeId: UUID): CharacterArc =
        getCharacterArcByCharacterAndThemeId(Character.Id(characterId), Theme.Id(themeId))
            ?: throw CharacterArcDoesNotExist(characterId, themeId)
    suspend fun replaceCharacterArcs(vararg characterArcs: CharacterArc)
    suspend fun getCharacterArcContainingArcSection(arcSectionId: CharacterArcSection.Id): CharacterArc?
    suspend fun getCharacterArcWithArcSectionOrError(arcSectionId: CharacterArcSection.Id): CharacterArc =
        getCharacterArcContainingArcSection(arcSectionId) ?: throw CharacterArcSectionDoesNotExist(arcSectionId.uuid)
    suspend fun listCharacterArcsForCharacter(characterId: Character.Id): List<CharacterArc>
    suspend fun getCharacterArcsContainingArcSections(arcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArc>
    suspend fun getCharacterArcsWithSectionsLinkedToLocation(locationId: Location.Id): List<CharacterArc>
    suspend fun updateCharacterArcs(characterArcs: Set<CharacterArc>)
    suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc>
    suspend fun listAllCharacterArcsInTheme(themeId: Theme.Id): List<CharacterArc>
    suspend fun removeCharacterArcs(vararg characterArcs: CharacterArc)
}