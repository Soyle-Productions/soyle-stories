package com.soyle.stories.theme.usecases.removeCharacterFromComparison

import com.soyle.stories.theme.ThemeException
import java.util.*

interface RemoveCharacterFromComparison {
    suspend operator fun invoke(themeId: UUID, characterId: UUID, outputPort: OutputPort)

    class ResponseModel(val themeId: UUID, val characterId: UUID, val themeDeleted: Boolean)

    interface OutputPort {
        fun receiveRemoveCharacterFromComparisonFailure(failure: ThemeException)
        fun receiveRemoveCharacterFromComparisonResponse(response: ResponseModel)
    }
}