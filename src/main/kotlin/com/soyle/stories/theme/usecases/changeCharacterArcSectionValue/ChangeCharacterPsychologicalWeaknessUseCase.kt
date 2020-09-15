package com.soyle.stories.theme.usecases.changeCharacterArcSectionValue

import com.soyle.stories.common.PsychologicalWeakness
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.repositories.getThemeOrError

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