package com.soyle.stories.theme.usecases.removeCharacterFromComparison

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.theme.ThemeException
import java.util.*

interface RemoveCharacterFromComparison {
    suspend operator fun invoke(themeId: UUID, characterId: UUID, outputPort: OutputPort)

    interface OutputPort {
        fun receiveRemoveCharacterFromComparisonResponse(response: RemovedCharacterFromTheme)
        suspend fun characterArcDeleted(response: DeletedCharacterArc)
    }
}