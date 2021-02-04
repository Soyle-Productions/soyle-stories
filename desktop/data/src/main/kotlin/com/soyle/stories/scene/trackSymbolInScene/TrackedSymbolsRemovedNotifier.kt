package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.entities.TrackedSymbolRemoved

class TrackedSymbolsRemovedNotifier : Notifier<TrackedSymbolsRemovedReceiver>(), TrackedSymbolsRemovedReceiver {
    override suspend fun receiveTrackedSymbolsRemoved(trackedSymbolsRemoved: List<TrackedSymbolRemoved>) {
        notifyAll { it.receiveTrackedSymbolsRemoved(trackedSymbolsRemoved) }
    }
}