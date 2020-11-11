package com.soyle.stories.theme.usecases.examineCentralConflictOfTheme

import com.soyle.stories.common.Desire
import com.soyle.stories.common.MoralWeakness
import com.soyle.stories.common.PsychologicalWeakness
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.characterInTheme.StoryFunction
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class ExamineCentralConflictOfThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : ExamineCentralConflictOfTheme {

    override suspend fun invoke(
        themeId: UUID,
        characterId: UUID?,
        outputPort: ExamineCentralConflictOfTheme.OutputPort
    ) {
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
        return examineCharacterChange(theme, majorCharacter, characterArcSections)
    }

    private fun examineCharacterChange(
        theme: Theme,
        majorCharacter: MajorCharacter,
        characterArcSections: Map<CharacterArcTemplateSection.Id, CharacterArcSection>
    ): ExaminedCharacterChange {
        return ExaminedCharacterChange(
            majorCharacter.id.uuid,
            majorCharacter.name,
            characterArcSections[Desire.id]?.value ?: "",
            characterArcSections[PsychologicalWeakness.id]?.value ?: "",
            characterArcSections[MoralWeakness.id]?.value ?: "",
            majorCharacter.characterChange,
            getOpponents(majorCharacter, theme)
        )
    }

    private fun getOpponents(
        majorCharacter: MajorCharacter,
        theme: Theme
    ): CharacterChangeOpponents {
        return CharacterChangeOpponents(majorCharacter.getOpponents().map {
            val opponent = theme.getIncludedCharacterById(it.key)!!
            CharacterChangeOpponent(
                it.key.uuid,
                opponent.name,
                majorCharacter.getAttacksByCharacter(opponent.id) ?: "",
                theme.getSimilarities(majorCharacter.id, opponent.id).fold({ "" }, { it }),
                opponent.position,
                it.value == StoryFunction.MainAntagonist
            )
        })
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
    ): Map<CharacterArcTemplateSection.Id, CharacterArcSection> {
        return characterArcRepository.getCharacterArcByCharacterAndThemeId(
            majorCharacter.id, theme.id
        )!!.arcSections.associateBy { it.template.id }
    }
}