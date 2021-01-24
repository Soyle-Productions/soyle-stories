package com.soyle.stories.theme.usecases.removeCharacterFromComparison

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison.OutputPort
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

    private fun getCharacterInTheme(characterId: UUID, theme: Theme) =
        theme.getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, characterId)

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