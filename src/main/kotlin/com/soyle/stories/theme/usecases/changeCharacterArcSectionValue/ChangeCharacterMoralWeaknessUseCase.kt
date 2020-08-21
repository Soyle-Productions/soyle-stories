package com.soyle.stories.theme.usecases.changeCharacterArcSectionValue

import com.soyle.stories.common.MoralWeakness
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

class ChangeCharacterMoralWeaknessUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : ChangeCharacterMoralWeakness {

    override suspend fun invoke(
        request: ChangeCharacterMoralWeakness.RequestModel,
        output: ChangeCharacterMoralWeakness.OutputPort
    ) {

        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))

        val character = getMajorCharacter(theme, request)

        val arcSection = getMoralWeaknessArcSection(character)

        characterArcSectionRepository.updateCharacterArcSection(
            arcSection.changeValue(request.moralWeakness)
        )

        output.characterMoralWeaknessChanged(
            ChangeCharacterMoralWeakness.ResponseModel(
                ChangedCharacterArcSectionValue(arcSection.id.uuid, character.id.uuid, theme.id.uuid, ArcSectionType.MoralWeakness, request.moralWeakness)
            )
        )
    }

    private suspend fun getMoralWeaknessArcSection(character: CharacterInTheme): CharacterArcSection {
        val thematicDesire =
            character.thematicSections.find { it.template.characterArcTemplateSectionId == MoralWeakness.id }!!
        return characterArcSectionRepository.getCharacterArcSectionById(thematicDesire.characterArcSectionId)!!
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