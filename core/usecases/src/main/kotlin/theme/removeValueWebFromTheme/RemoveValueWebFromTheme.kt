package com.soyle.stories.usecase.theme.removeValueWebFromTheme

import java.util.*

interface RemoveValueWebFromTheme {

    suspend operator fun invoke(valueWebId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun removedValueWebFromTheme(response: ValueWebRemovedFromTheme)
    }

}