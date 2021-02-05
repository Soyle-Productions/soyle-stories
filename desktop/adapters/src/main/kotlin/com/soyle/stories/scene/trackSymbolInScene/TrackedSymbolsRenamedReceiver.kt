package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.TrackedSymbolRenamed

interface TrackedSymbolsRenamedReceiver {
    suspend fun receiveTrackedSymbolsRenamed(trackedSymbolsRenamed: List<TrackedSymbolRenamed>)
}