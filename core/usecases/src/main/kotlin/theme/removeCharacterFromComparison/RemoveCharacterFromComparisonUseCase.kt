package com.soyle.stories.usecase.theme.removeCharacterFromComparison

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.usecase.character.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemoveCharacterFromComparison.OutputPort
import java.util.*

class RemoveCharacterFromComparisonUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : RemoveCharacterFromComparison {

    override suspend fun invoke(themeId: UUID, characterId: UUID, outputPort: OutputPort) {
        val theme = getTheme(themeId)
        val characterInTheme = getCharacterInTheme(characterId, theme)

        val deletedCharacterArc = removeCharacterArcFromMajorCharacter(characterInTheme, theme.id)

        themeRepository.updateTheme(theme.withoutCharacter(characterInTheme.id))
        val response = RemovedCharacterFromTheme(themeId, characterId)

        deletedCharacterArc?.let { outputPort.characterArcDeleted(it) }
        outputPort.receiveRemoveCharacterFromComparisonResponse(response)
    }

    private suspend fun getTheme(themeId: UUID): Theme
    {
        return themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    private fun getCharacterInTheme(characterId: UUID, theme: Theme): CharacterInTheme {
        return theme.getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, characterId)
    }

    private suspend fun removeCharacterArcFromMajorCharacter(
        characterInTheme: CharacterInTheme,
        themeId: Theme.Id
    ): DeletedCharacterArc? {
        if (characterInTheme is MajorCharacter) {
            val characterArc = characterArcRepository.getCharacterArcByCharacterAndThemeId(characterInTheme.id, themeId)!!
            return DeletedCharacterArc(characterArc.characterId.uuid, themeId.uuid)
        }
        return null
    }

}