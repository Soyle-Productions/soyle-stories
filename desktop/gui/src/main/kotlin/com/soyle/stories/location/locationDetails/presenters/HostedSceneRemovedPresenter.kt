package com.soyle.stories.location.locationDetails.presenters

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.location.hostedScene.HostedSceneRemovedReceiver
import com.soyle.stories.location.locationDetails.LocationDetailsViewModel

class HostedSceneRemovedPresenter(
    private val locationId: Location.Id,
    private val view: LocationDetailsViewModel
) : HostedSceneRemovedReceiver {

    override suspend fun receiveHostedScenesRemoved(events: List<HostedSceneRemoved>) {
        val relevantEvents = events.filter { it.locationId == locationId }
        if (relevantEvents.isEmpty()) return
        val removedSceneIds = relevantEvents.map { it.sceneId }.toSet()
        with(view) {
            update {
                hostedScenes = hostedScenes.filterNot { it.sceneId in removedSceneIds }
            }
        }
    }
}