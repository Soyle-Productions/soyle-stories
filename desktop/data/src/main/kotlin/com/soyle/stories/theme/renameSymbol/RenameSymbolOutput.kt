package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver
import com.soyle.stories.scene.trackSymbolInScene.TrackedSymbolsRenamedReceiver
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol

class RenameSymbolOutput(
    private val renamedSymbolReceiver: RenamedSymbolReceiver,
    private val trackedSymbolsRenamedReceiver: TrackedSymbolsRenamedReceiver,
    private val mentionTextReplacedReceiver: MentionTextReplacedReceiver
) : RenameSymbol.OutputPort {

    override suspend fun symbolRenamed(response: RenameSymbol.ResponseModel) {
        renamedSymbolReceiver.receiveRenamedSymbol(response.renamedSymbol)
        if (response.trackedSymbolsRenamed.isNotEmpty()) {
            trackedSymbolsRenamedReceiver.receiveTrackedSymbolsRenamed(response.trackedSymbolsRenamed)
        }
        response.mentionTextReplaced.forEach {
            mentionTextReplacedReceiver.receiveMentionTextReplaced(it)
        }
    }
}