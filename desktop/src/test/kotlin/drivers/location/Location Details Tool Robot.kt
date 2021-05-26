package com.soyle.stories.desktop.config.drivers.location

import com.soyle.stories.desktop.view.location.details.`Location Details View Access`.Companion.drive
import com.soyle.stories.di.get
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.location.locationDetails.LocationDetails
import com.soyle.stories.location.locationDetails.LocationDetailsScope
import com.soyle.stories.location.locationList.LocationList
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench

fun LocationList.givenLocationDetailsToolHasBeenOpenedFor(location: Location): LocationDetails {
    return (scope as ProjectScope).getLocationDetailsToolForLocation(location) ?: run {
        openLocationDetails(location.id)
        (scope as ProjectScope).getLocationDetailsToolForLocation(location)
            ?: error("Location details tool was not opened for $location")
    }
}

fun ProjectScope.getLocationDetailsToolForLocation(location: Location): LocationDetails? {
    val toolScope = toolScopes
        .filterIsInstance<LocationDetailsScope>()
        .find { it.locationId == location.id.uuid.toString() }
    return toolScope?.get()
}

fun LocationDetails.requestScenesToHost()
{
    drive {
        addSceneButton.fire()
    }
}

fun LocationDetails.addScene(scene: Scene)
{
    drive {
        val sceneItem = availableScenesToHost!!.getSceneItem(scene.id)!!
        sceneItem.fire()
    }
}