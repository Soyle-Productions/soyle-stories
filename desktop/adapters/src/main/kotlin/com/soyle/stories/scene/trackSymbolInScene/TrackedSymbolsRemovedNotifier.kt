package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.TrackedSymbolRemoved

class TrackedSymbolsRemovedNotifier : Notifier<TrackedSymbolsRemovedReceiver>(), TrackedSymbolsRemovedReceiver {
    override suspend fun receiveTrackedSymbolsRemoved(trackedSymbolsRemoved: List<TrackedSymbolRemoved>) {
        notifyAll { it.receiveTrackedSymbolsRemoved(trackedSymbolsRemoved) }
    }
}