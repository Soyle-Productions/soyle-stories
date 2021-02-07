package com.soyle.stories.usecase.theme.deleteTheme

import com.soyle.stories.usecase.character.deleteCharacterArc.DeletedCharacterArc
import java.util.*

interface DeleteTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun themeDeleted(response: DeletedTheme)
        suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>)
    }

}