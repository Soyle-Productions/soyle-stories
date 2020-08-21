package com.soyle.stories.theme.usecases.deleteTheme

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
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

        val deletedCharacterArcs = theme.characters.filterIsInstance<MajorCharacter>().map {
            it.characterArc
        }

        themeRepository.deleteTheme(theme)

        if (deletedCharacterArcs.isNotEmpty()) {
            output.characterArcsDeleted(deletedCharacterArcs.map {
                DeletedCharacterArc(it.characterId.uuid, theme.id.uuid)
            })
        }
        output.themeDeleted(DeletedTheme(themeId))
    }
}