package com.soyle.stories.usecase.theme.removeCharacterFromComparison

import com.soyle.stories.usecase.character.arc.deleteCharacterArc.DeletedCharacterArc
import java.util.*

interface RemoveCharacterFromComparison {
    suspend operator fun invoke(themeId: UUID, characterId: UUID, outputPort: OutputPort)

    interface OutputPort {
        suspend fun receiveRemoveCharacterFromComparisonResponse(response: RemovedCharacterFromTheme)
        suspend fun characterArcDeleted(response: DeletedCharacterArc)
    }
}