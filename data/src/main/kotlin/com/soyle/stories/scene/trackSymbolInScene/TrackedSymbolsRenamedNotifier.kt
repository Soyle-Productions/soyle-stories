package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.entities.TrackedSymbolRenamed

class TrackedSymbolsRenamedNotifier : Notifier<TrackedSymbolsRenamedReceiver>(), TrackedSymbolsRenamedReceiver {
    override suspend fun receiveTrackedSymbolsRenamed(trackedSymbolsRenamed: List<TrackedSymbolRenamed>) {
        notifyAll { it.receiveTrackedSymbolsRenamed(trackedSymbolsRenamed) }
    }
}