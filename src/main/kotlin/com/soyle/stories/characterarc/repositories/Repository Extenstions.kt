package com.soyle.stories.characterarc.repositories

import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcRepository
import java.util.*

suspend fun CharacterArcRepository.getCharacterArcOrError(characterId: UUID, themeId: UUID): CharacterArc
 = getCharacterArcByCharacterAndThemeId(Character.Id(characterId), Theme.Id(themeId))
    ?: throw CharacterArcDoesNotExist(characterId, themeId)

suspend fun CharacterArcRepository.getCharacterArcWithArcSectionOrError(arcSectionId: UUID) =
    (getCharacterArcContainingArcSection(CharacterArcSection.Id(arcSectionId))
        ?: throw CharacterArcSectionDoesNotExist(arcSectionId))