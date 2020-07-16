package com.soyle.stories.theme.usecases.deleteTheme

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class DeleteThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : DeleteTheme {

    override suspend fun invoke(themeId: UUID, output: DeleteTheme.OutputPort) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        val majorCharacters = theme.characters.filterIsInstance<MajorCharacter>().onEach {
            val baseCharacter = characterRepository.getCharacterById(it.id)!!
            characterRepository.updateCharacter(baseCharacter.withoutCharacterArc(theme.id))
        }

        themeRepository.deleteTheme(theme)

        if (majorCharacters.isNotEmpty()) {
            output.characterArcsDeleted(majorCharacters.map {
                DeletedCharacterArc(it.id.uuid, theme.id.uuid)
            })
        }
        output.themeDeleted(DeletedTheme(themeId))
    }
}