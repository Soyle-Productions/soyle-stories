package com.soyle.stories.usecase.character.changeCharacterArcSectionValue

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.PsychologicalWeakness
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.theme.ThemeRepository

class ChangeCharacterPsychologicalWeaknessUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : ChangeCharacterPsychologicalWeakness {

    override suspend fun invoke(
        request: ChangeCharacterPsychologicalWeakness.RequestModel,
        output: ChangeCharacterPsychologicalWeakness.OutputPort
    ) {

        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))

        val character = getMajorCharacter(theme, request)

        val characterArc = characterArcRepository.getCharacterArcByCharacterAndThemeId(character.id, theme.id)!!

        val arcSection = characterArc.arcSections.find { it.template isSameEntityAs PsychologicalWeakness }!!

        characterArcRepository.replaceCharacterArcs(
            characterArc.withArcSectionsMapped {
                if (it.template isSameEntityAs PsychologicalWeakness) it.withValue(request.psychologicalWeakness)
                else it
            }
        )

        output.characterPsychologicalWeaknessChanged(
            ChangeCharacterPsychologicalWeakness.ResponseModel(
                ChangedCharacterArcSectionValue(arcSection.id.uuid, character.id.uuid, theme.id.uuid, ArcSectionType.PsychologicalWeakness, request.psychologicalWeakness)
            )
        )
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