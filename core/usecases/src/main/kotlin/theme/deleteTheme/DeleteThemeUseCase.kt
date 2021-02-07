package com.soyle.stories.usecase.theme.deleteTheme

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class DeleteThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : DeleteTheme {

    override suspend fun invoke(themeId: UUID, output: DeleteTheme.OutputPort) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        val characterArcsToDelete = characterArcRepository.listAllCharacterArcsInTheme(theme.id)

        themeRepository.deleteTheme(theme)

        if (characterArcsToDelete.isNotEmpty()) {
            characterArcRepository.removeCharacterArcs(*characterArcsToDelete.toTypedArray())
            output.characterArcsDeleted(characterArcsToDelete.map {
                DeletedCharacterArc(it.characterId.uuid, theme.id.uuid)
            })
        }
        output.themeDeleted(DeletedTheme(themeId))
    }
}