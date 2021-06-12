package com.soyle.stories.location.locationDetails.presenters

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRenamed
import com.soyle.stories.location.hostedScene.HostedSceneRenamedReceiver
import com.soyle.stories.location.locationDetails.HostedSceneItemViewModel
import com.soyle.stories.location.locationDetails.LocationDetailsViewModel

class HostedSceneRenamedPresenter(
    private val locationId: Location.Id,
    private val view: LocationDetailsViewModel
) : HostedSceneRenamedReceiver {

    override suspend fun receiveHostedScenesRenamed(events: List<HostedSceneRenamed>) {
        val relevantEvents = events.filter { it.locationId == locationId }
        if (relevantEvents.isEmpty()) return
        val eventsBySceneId = relevantEvents.associateBy { it.sceneId }
        with(view) {
            update {
                for (hostedScene in hostedScenes) {
                    val relatedEvent = eventsBySceneId.get(hostedScene.sceneId) ?: continue
                    hostedScene.name = relatedEvent.newName
                }
            }
        }
    }
}
