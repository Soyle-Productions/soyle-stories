package com.soyle.stories.theme.usecases.removeCharacterFromComparison

import com.soyle.stories.theme.LocalThemeException
import java.util.*

interface RemoveCharacterFromLocalComparison {

    suspend operator fun invoke(themeId: UUID, characterId: UUID, output: OutputPort)

    class ResponseModel(val themeId: UUID, val characterId: UUID, val themeRemoved: Boolean, val removedTools: List<UUID>)

    interface OutputPort {
        fun receiveRemoveCharacterFromLocalComparisonFailure(failure: LocalThemeException)
        fun receiveRemoveCharacterFromLocalComparisonResponse(response: ResponseModel)
    }
}