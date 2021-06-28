package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.events.TrackedSymbolRemoved

interface TrackedSymbolsRemovedReceiver {
    suspend fun receiveTrackedSymbolsRemoved(trackedSymbolsRemoved: List<TrackedSymbolRemoved>)
}