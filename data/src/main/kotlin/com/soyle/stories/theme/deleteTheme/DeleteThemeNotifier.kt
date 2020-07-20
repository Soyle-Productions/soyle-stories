package com.soyle.stories.theme.deleteTheme

import com.soyle.stories.character.deleteCharacterArc.DeleteCharacterArcNotifier
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter

class DeleteThemeNotifier(
    private val deleteCharacterArcNotifier: DeleteCharacterArcNotifier
) : Notifier<DeleteTheme.OutputPort>(), DeleteTheme.OutputPort {

    override fun themeDeleted(response: DeletedTheme) {
        notifyAll { it.themeDeleted(response) }
    }

    override suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>) {
        response.forEach {
            deleteCharacterArcNotifier.receiveDemoteMajorCharacterResponse(
                DemoteMajorCharacter.ResponseModel(it.themeId, it.characterId, listOf(), true)
            )
        }
    }

}