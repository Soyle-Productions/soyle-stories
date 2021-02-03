package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.TrackedSymbolRemoved

interface TrackedSymbolsRemovedReceiver {
    suspend fun receiveTrackedSymbolsRemoved(trackedSymbolsRemoved: List<TrackedSymbolRemoved>)
}