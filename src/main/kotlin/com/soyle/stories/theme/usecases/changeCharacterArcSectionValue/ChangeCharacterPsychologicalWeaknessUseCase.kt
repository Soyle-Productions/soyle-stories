package com.soyle.stories.theme.usecases.changeCharacterArcSectionValue

import com.soyle.stories.common.PsychologicalWeakness
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.repositories.getThemeOrError

class ChangeCharacterPsychologicalWeaknessUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : ChangeCharacterPsychologicalWeakness {

    override suspend fun invoke(
        request: ChangeCharacterPsychologicalWeakness.RequestModel,
        output: ChangeCharacterPsychologicalWeakness.OutputPort
    ) {

        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))

        val character = getMajorCharacter(theme, request)

        val arcSection = getPsychologicalWeaknessArcSection(character)

        characterArcSectionRepository.updateCharacterArcSection(
            arcSection.changeValue(request.psychologicalWeakness)
        )

        output.characterPsychologicalWeaknessChanged(
            ChangeCharacterPsychologicalWeakness.ResponseModel(
                ChangedCharacterArcSectionValue(arcSection.id.uuid, character.id.uuid, theme.id.uuid, ArcSectionType.PsychologicalWeakness, request.psychologicalWeakness)
            )
        )
    }

    private suspend fun getPsychologicalWeaknessArcSection(character: CharacterInTheme): CharacterArcSection {
        val thematicDesire =
            character.thematicSections.find { it.template.characterArcTemplateSectionId == PsychologicalWeakness.id }!!
        return characterArcSectionRepository.getCharacterArcSectionById(thematicDesire.characterArcSectionId)!!
    }

    private fun getMajorCharacter(
        theme: Theme,
        request: ChangeCharacterPsychologicalWeakness.RequestModel
    ): CharacterInTheme {
        val character = theme.getIncludedCharacterById(Character.Id(request.characterId))
            ?: throw CharacterNotInTheme(request.themeId, request.characterId)

        character as? MajorCharacter ?: throw CharacterIsNotMajorCharacterInTheme(request.characterId, request.themeId)
        return character
    }
}