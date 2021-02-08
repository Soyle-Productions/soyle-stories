package com.soyle.stories.theme.deleteTheme

import com.soyle.stories.character.deleteCharacterArc.DeleteCharacterArcNotifier
import com.soyle.stories.usecase.character.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.usecase.theme.deleteTheme.DeleteTheme
import com.soyle.stories.usecase.theme.deleteTheme.DeletedTheme
import com.soyle.stories.usecase.theme.demoteMajorCharacter.DemoteMajorCharacter

class DeleteThemeOutput(
    private val themeDeletedReceiver: ThemeDeletedReceiver,
    private val deleteCharacterArcNotifier: DeleteCharacterArcNotifier
) : DeleteTheme.OutputPort {

    override suspend fun themeDeleted(response: DeletedTheme) {
        themeDeletedReceiver.receiveDeletedTheme(response)
    }

    override suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>) {
        response.forEach {
            deleteCharacterArcNotifier.receiveDemoteMajorCharacterResponse(
                DemoteMajorCharacter.ResponseModel(it.themeId, it.characterId, listOf(), true)
            )
        }
    }

}