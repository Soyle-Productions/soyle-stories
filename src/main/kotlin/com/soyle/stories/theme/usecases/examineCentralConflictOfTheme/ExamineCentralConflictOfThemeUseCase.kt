package com.soyle.stories.theme.usecases.examineCentralConflictOfTheme

import com.soyle.stories.common.Desire
import com.soyle.stories.common.MoralWeakness
import com.soyle.stories.common.PsychologicalWeakness
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class ExamineCentralConflictOfThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : ExamineCentralConflictOfTheme {

    override suspend fun invoke(themeId: UUID, characterId: UUID?, outputPort: ExamineCentralConflictOfTheme.OutputPort) {
        val theme = getTheme(themeId)

        val characterChange = if (characterId != null) {
            getExaminedCharacterChange(theme, characterId)
        } else null

        outputPort.centralConflictExamined(ExaminedCentralConflict(themeId, theme.centralConflict, characterChange))
    }

    private suspend fun getExaminedCharacterChange(
        theme: Theme,
        characterId: UUID
    ): ExaminedCharacterChange {
        themeMustContainCharacter(theme, characterId)
        val majorCharacter = getMajorCharacter(theme, characterId)
        val characterArcSections = getArcSectionsByTemplate(majorCharacter, theme)
        return examineCharacterChange(majorCharacter, characterArcSections)
    }

    private fun examineCharacterChange(
        majorCharacter: MajorCharacter,
        characterArcSections: Map<CharacterArcTemplateSection, CharacterArcSection>
    ): ExaminedCharacterChange {
        return ExaminedCharacterChange(
            majorCharacter.id.uuid,
            majorCharacter.name,
            characterArcSections[Desire]?.value ?: "",
            characterArcSections[PsychologicalWeakness]?.value ?: "",
            characterArcSections[MoralWeakness]?.value ?: "",
            majorCharacter.characterChange
        )
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

    private fun themeMustContainCharacter(
        theme: Theme,
        characterId: UUID
    ) {
        if (!theme.containsCharacter(Character.Id(characterId)))
            throw CharacterNotInTheme(theme.id.uuid, characterId)
    }

    private fun getMajorCharacter(
        theme: Theme,
        characterId: UUID
    ) = (theme.getMajorCharacterById(Character.Id(characterId))
        ?: throw CharacterIsNotMajorCharacterInTheme(characterId, theme.id.uuid))

    private suspend fun getArcSectionsByTemplate(
        majorCharacter: MajorCharacter,
        theme: Theme
    ): Map<CharacterArcTemplateSection, CharacterArcSection> {
        return characterArcSectionRepository.getCharacterArcSectionsForCharacterInTheme(
            majorCharacter.id, theme.id
        ).associateBy { it.template }
    }
}