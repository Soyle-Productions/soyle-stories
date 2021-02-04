package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.prose.ContentReplaced
import com.soyle.stories.prose.editProse.ContentReplacedReceiver
import com.soyle.stories.scene.usecases.trackSymbolInScene.SynchronizeTrackedSymbolsWithProse

class SynchronizeTrackedSymbolsWithProseController(
    private val synchronizeTrackedSymbolsWithProse: SynchronizeTrackedSymbolsWithProse,
    private val synchronizeTrackedSymbolsWithProseOutput: SynchronizeTrackedSymbolsWithProse.OutputPort
) : ContentReplacedReceiver {

    override suspend fun receiveContentReplacedEvent(contentReplaced: ContentReplaced) {
        synchronizeTrackedSymbolsWithProse.invoke(contentReplaced.proseId, synchronizeTrackedSymbolsWithProseOutput)
    }
}