package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.TrackedSymbolRenamed

interface TrackedSymbolsRenamedReceiver {
    suspend fun receiveTrackedSymbolsRenamed(trackedSymbolsRenamed: List<TrackedSymbolRenamed>)
}