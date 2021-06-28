package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.events.TrackedSymbolRenamed

interface TrackedSymbolsRenamedReceiver {
    suspend fun receiveTrackedSymbolsRenamed(trackedSymbolsRenamed: List<TrackedSymbolRenamed>)
}