package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.MoralWeakness
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.theme.ThemeRepository


class ChangeCharacterMoralWeaknessUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : ChangeCharacterMoralWeakness {

    override suspend fun invoke(
        request: ChangeCharacterMoralWeakness.RequestModel,
        output: ChangeCharacterMoralWeakness.OutputPort
    ) {

        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))

        val character = getMajorCharacter(theme, request)

        val characterArc = characterArcRepository.getCharacterArcByCharacterAndThemeId(character.id, theme.id)!!

        val arcSection = characterArc.arcSections.find { it.template isSameEntityAs MoralWeakness }!!

        characterArcRepository.replaceCharacterArcs(
            characterArc.withArcSectionsMapped {
                if (it.template isSameEntityAs MoralWeakness) it.withValue(request.moralWeakness)
                else it
            }
        )

        output.characterMoralWeaknessChanged(
            ChangeCharacterMoralWeakness.ResponseModel(
                ChangedCharacterArcSectionValue(arcSection.id.uuid, character.id.uuid, theme.id.uuid, ArcSectionType.MoralWeakness, request.moralWeakness)
            )
        )
    }

    private fun getMajorCharacter(
        theme: Theme,
        request: ChangeCharacterMoralWeakness.RequestModel
    ): CharacterInTheme {
        val character = theme.getIncludedCharacterById(Character.Id(request.characterId))
            ?: throw CharacterNotInTheme(request.themeId, request.characterId)

        character as? MajorCharacter ?: throw CharacterIsNotMajorCharacterInTheme(request.characterId, request.themeId)
        return character
    }
}