package com.soyle.stories.usecase.theme.outlineMoralArgument

import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.character.CharacterArcDoesNotExist
import java.util.*

class OutlineMoralArgument(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : GetMoralArgumentFrame, OutlineMoralArgumentForCharacterInTheme {

    override suspend fun invoke(themeId: UUID, output: GetMoralArgumentFrame.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        output.receiveMoralArgumentFrame(
            GetMoralArgumentFrame.ResponseModel(
                themeId,
                theme.themeLine,
                theme.centralMoralProblem,
                theme.thematicRevelation
            )
        )
    }

    override suspend fun invoke(
        themeId: UUID,
        characterId: UUID,
        output: OutlineMoralArgumentForCharacterInTheme.OutputPort
    ) {
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
                    OutlineMoralArgumentForCharacterInTheme.CharacterArcSectionInMoralArgument(
                        it.id.uuid,
                        it.value,
                        it.template.name,
                        it.template.isRequired
                    )
                }
            )
        )
    }
}