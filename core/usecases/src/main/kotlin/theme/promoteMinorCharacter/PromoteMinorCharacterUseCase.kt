package com.soyle.stories.usecase.theme.promoteMinorCharacter

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.theme.CharacterArcAlreadyExistsForCharacterInTheme
import com.soyle.stories.domain.theme.CharacterIsAlreadyMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.MinorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.arc.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository

class PromoteMinorCharacterUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : PromoteMinorCharacter {
    override suspend fun invoke(
        request: PromoteMinorCharacter.RequestModel,
        output: PromoteMinorCharacter.OutputPort
    ) {
        val theme = themeRepository.getThemeById(Theme.Id(request.themeId))
            ?: throw ThemeDoesNotExist(request.themeId)
        val characterInTheme = theme.getIncludedCharacterById(Character.Id(request.characterId))
            ?: throw CharacterNotInTheme(
                request.themeId,
                request.characterId
            )

        if (characterInTheme !is MinorCharacter) {
            throw CharacterIsAlreadyMajorCharacterInTheme(
                request.characterId,
                request.themeId
            )
        }

        val arcsInTheme = characterArcRepository.listAllCharacterArcsInTheme(theme.id)
        val existingArc = arcsInTheme.find { it.characterId == characterInTheme.id }
        if (existingArc != null) {
            throw CharacterArcAlreadyExistsForCharacterInTheme(
                request.characterId,
                request.themeId
            )
        }
        val promotionResult = theme.withCharacterPromoted(characterInTheme.id)
        themeRepository.updateTheme(promotionResult)
        characterArcRepository.addNewCharacterArc(CharacterArc.planNewCharacterArc(characterInTheme.id, theme.id, theme.name))
        output.receivePromoteMinorCharacterResponse(
            PromoteMinorCharacter.ResponseModel(
                CreatedCharacterArc(
                    request.themeId,
                    request.characterId,
                    promotionResult.name
                )
            )
        )
    }
}