package com.soyle.stories.theme.usecases.changeCharacterArcSectionValue

import com.soyle.stories.common.Desire
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.repositories.getThemeOrError
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangeCharacterDesire.*

class ChangeCharacterDesireUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : ChangeCharacterDesire {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))

        val character = getMajorCharacter(theme, request)

        val arcSection = getDesireArcSection(character)

        characterArcSectionRepository.updateCharacterArcSection(
            arcSection.changeValue(request.desire)
        )

        output.characterDesireChanged(
            ResponseModel(
                ChangedCharacterArcSectionValue(arcSection.id.uuid, character.id.uuid, theme.id.uuid, request.desire)
            )
        )
    }

    private suspend fun getDesireArcSection(character: CharacterInTheme): CharacterArcSection {
        val thematicDesire =
            character.thematicSections.find { it.template.characterArcTemplateSectionId == Desire.id }!!
        return characterArcSectionRepository.getCharacterArcSectionById(thematicDesire.characterArcSectionId)!!
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