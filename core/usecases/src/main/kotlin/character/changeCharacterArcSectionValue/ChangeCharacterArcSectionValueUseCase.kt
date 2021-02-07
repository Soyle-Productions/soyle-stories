package com.soyle.stories.usecase.character.changeCharacterArcSectionValue

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ChangeCharacterArcSectionValueUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : ChangeCharacterArcSectionValue {

    override suspend fun invoke(
        request: ChangeCharacterArcSectionValue.RequestModel,
        output: ChangeCharacterArcSectionValue.OutputPort
    ) {
        val characterArc = getCharacterArc(request.themeId, request.characterId)

        characterArcRepository.replaceCharacterArcs(
            characterArc.withArcSectionsMapped {
                    if (it.id.uuid == request.arcSectionId) it.withValue(request.newValue)
                    else it
                }
        )

        output.characterArcSectionValueChanged(
            ChangeCharacterArcSectionValue.ResponseModel(
                ChangedCharacterArcSectionValue(
                    request.arcSectionId,
                    request.characterId,
                    request.themeId,
                    null,
                    request.newValue
                )
            )
        )

    }

    private suspend fun getCharacterArc(themeId: UUID, characterUUID: UUID): CharacterArc
    {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        val characterId = Character.Id(characterUUID)

        validateThemeHasMajorCharacter(theme, characterId)

        return characterArcRepository.getCharacterArcByCharacterAndThemeId(characterId, theme.id)!!
    }

    private fun validateThemeHasMajorCharacter(theme: Theme, characterId: Character.Id)
    {
        if (! theme.containsCharacter(characterId)) throw CharacterNotInTheme(theme.id.uuid, characterId.uuid)
        if (! theme.characterIsMajorCharacter(characterId)) throw CharacterIsNotMajorCharacterInTheme(characterId.uuid, theme.id.uuid)
    }

}