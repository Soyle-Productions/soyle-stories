package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.prose.events.ContentReplaced
import com.soyle.stories.prose.editProse.ContentReplacedReceiver
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.SynchronizeTrackedSymbolsWithProse

class SynchronizeTrackedSymbolsWithProseController(
    private val synchronizeTrackedSymbolsWithProse: SynchronizeTrackedSymbolsWithProse,
    private val synchronizeTrackedSymbolsWithProseOutput: SynchronizeTrackedSymbolsWithProse.OutputPort
) : ContentReplacedReceiver {

    override suspend fun receiveContentReplacedEvent(contentReplaced: ContentReplaced) {
        synchronizeTrackedSymbolsWithProse.invoke(contentReplaced.proseId, synchronizeTrackedSymbolsWithProseOutput)
    }
}