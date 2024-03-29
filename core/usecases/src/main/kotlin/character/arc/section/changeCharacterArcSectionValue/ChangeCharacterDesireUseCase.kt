package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.Desire
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterDesire.*
import com.soyle.stories.usecase.theme.ThemeRepository

class ChangeCharacterDesireUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : ChangeCharacterDesire {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))

        val character = getMajorCharacter(theme, request)

        val characterArc = characterArcRepository.getCharacterArcByCharacterAndThemeId(character.id, theme.id)!!

        val arcSection = characterArc.arcSections.find { it.template isSameEntityAs Desire }!!

        characterArcRepository.replaceCharacterArcs(
            characterArc.withArcSectionsMapped {
                if (it.template isSameEntityAs Desire) it.withValue(request.desire)
                else it
            }
        )

        output.characterDesireChanged(
            ResponseModel(
                ChangedCharacterArcSectionValue(arcSection.id.uuid, character.id.uuid, theme.id.uuid, ArcSectionType.Desire, request.desire)
            )
        )
    }

    private fun getMajorCharacter(
        theme: Theme,
        request: RequestModel
    ): CharacterInTheme {
        val character = theme.getIncludedCharacterById(Character.Id(request.characterId))
            ?: throw CharacterNotInTheme(request.themeId, request.characterId)

        character as? MajorCharacter ?: throw CharacterIsNotMajorCharacterInTheme(request.characterId, request.themeId)
        return character
    }

}