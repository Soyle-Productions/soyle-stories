package com.soyle.stories.theme.usecases.deleteTheme

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import java.util.*

interface DeleteTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        fun themeDeleted(response: DeletedTheme)
        suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>)
    }

}