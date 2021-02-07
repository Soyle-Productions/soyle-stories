package com.soyle.stories.usecase.theme.renameSymbol

import com.soyle.stories.domain.prose.MentionTextReplaced
import com.soyle.stories.domain.scene.TrackedSymbolRenamed
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface RenameSymbol {

    suspend operator fun invoke(symbolId: UUID, name: NonBlankString, output: OutputPort)

    class ResponseModel(
        val renamedSymbol: RenamedSymbol,
        val trackedSymbolsRenamed: List<TrackedSymbolRenamed>,
        val mentionTextReplaced: List<MentionTextReplaced>
    )

    interface OutputPort {
        suspend fun symbolRenamed(response: ResponseModel)
    }

}