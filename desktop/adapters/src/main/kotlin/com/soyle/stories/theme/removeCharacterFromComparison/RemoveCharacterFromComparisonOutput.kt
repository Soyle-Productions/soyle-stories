package com.soyle.stories.theme.removeCharacterFromComparison

import com.soyle.stories.character.deleteCharacterArc.DeleteCharacterArcNotifier
import com.soyle.stories.usecase.character.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.usecase.theme.demoteMajorCharacter.DemoteMajorCharacter
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemoveCharacterFromComparison
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemovedCharacterFromTheme

class RemoveCharacterFromComparisonOutput(
    private val removedCharacterFromThemeReceiver: RemovedCharacterFromThemeReceiver,
    private val deleteCharacterArcNotifier: DeleteCharacterArcNotifier
) : RemoveCharacterFromComparison.OutputPort {

    override suspend fun receiveRemoveCharacterFromComparisonResponse(response: RemovedCharacterFromTheme) {
        removedCharacterFromThemeReceiver.receiveRemovedCharacterFromTheme(response)
    }

    override suspend fun characterArcDeleted(response: DeletedCharacterArc) {
        deleteCharacterArcNotifier.receiveDemoteMajorCharacterResponse(
            DemoteMajorCharacter.ResponseModel(response.themeId, response.characterId, listOf(), false)
        )
    }

}