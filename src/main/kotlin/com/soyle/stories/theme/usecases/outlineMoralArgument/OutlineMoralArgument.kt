package com.soyle.stories.theme.usecases.outlineMoralArgument

import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.repositories.getThemeOrError
import java.util.*

class OutlineMoralArgument(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : GetMoralProblemAndThemeLineInTheme, OutlineMoralArgumentForCharacterInTheme {

    override suspend fun invoke(themeId: UUID, output: GetMoralProblemAndThemeLineInTheme.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        output.receiveMoralProblemAndThemeLineInTheme(GetMoralProblemAndThemeLineInTheme.ResponseModel(
            themeId,
            theme.themeLine,
            theme.centralMoralProblem))
    }

    override suspend fun invoke(themeId: UUID, characterId: UUID, output: OutlineMoralArgumentForCharacterInTheme.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        val includedCharacter = theme.getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(themeId, characterId)
        val majorCharacter = theme.getMajorCharacterById(includedCharacter.id)
            ?: throw CharacterIsNotMajorCharacterInTheme(characterId, themeId)

        // if there is no character arc, something really wrong has happened.
        val characterArc = characterArcRepository.getCharacterArcByCharacterAndThemeId(majorCharacter.id, theme.id)
            ?: throw CharacterArcDoesNotExist(majorCharacter.id.uuid, theme.id.uuid)

        output.receiveMoralArgumentOutlineForCharacterInTheme(
            OutlineMoralArgumentForCharacterInTheme.ResponseModel(
                majorCharacter.id.uuid,
                majorCharacter.name,
                characterArc.moralArgument().arcSections.map {
                    OutlineMoralArgumentForCharacterInTheme.CharacterArcSectionInMoralArgument(it.id.uuid, it.value, it.template.name, it.template.isRequired)
                }
            )
        )
    }
}