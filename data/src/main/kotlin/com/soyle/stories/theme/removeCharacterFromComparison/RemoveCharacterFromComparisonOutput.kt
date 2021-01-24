package com.soyle.stories.theme.removeCharacterFromComparison

import com.soyle.stories.character.deleteCharacterArc.DeleteCharacterArcNotifier
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme
import kotlin.coroutines.coroutineContext

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