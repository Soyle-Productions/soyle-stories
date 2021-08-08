package com.soyle.stories.desktop.config.drivers.location

import com.soyle.stories.desktop.view.location.details.`Location Details Access`.Companion.access
import com.soyle.stories.desktop.view.location.details.`Location Details Access`.Companion.drive
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.di.get
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.config.dynamic.LocationDetails
import com.soyle.stories.location.details.LocationDetailsView
import com.soyle.stories.location.details.LocationDetailsScope
import com.soyle.stories.location.locationList.LocationList
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialog
import javafx.event.ActionEvent
import javafx.scene.control.MenuButton

fun LocationList.givenLocationDetailsToolHasBeenOpenedFor(location: Location): LocationDetailsView {
    return (scope as ProjectScope).getLocationDetailsToolForLocation(location) ?: run {
        openLocationDetails(location.id)
        (scope as ProjectScope).getLocationDetailsToolForLocation(location)
            ?: error("Location details tool was not opened for $location")
    }
}

fun ProjectScope.getLocationDetailsToolForLocation(location: Location): LocationDetailsView? {
    val toolScope = toolScopes
        .filterIsInstance<LocationDetailsScope>()
        .find { it.locationId == location.id.uuid.toString() }
    return toolScope?.get()
}

fun LocationDetailsView.givenRequestedSceneToHost(): LocationDetailsView
{
    if ((access().hostSceneButton?.button as? MenuButton)?.isShowing != true) {
        requestScenesToHost()
    }
    return this
}

fun LocationDetailsView.requestScenesToHost()
{
    drive {
        hostSceneButton!!.button.fire()
    }
}

fun LocationDetailsView.selectCreateSceneOption(): CreateSceneDialog
{
    return drive {
        if ((hostSceneButton?.button as? MenuButton)?.isShowing != true) hostSceneButton!!.button.fire()
        hostSceneButton!!.createSceneItem!!.fire()
        getOpenDialog<CreateSceneDialog>() ?: error("Create Scene Dialog was not opened")
    }
}

fun LocationDetailsView.addScene(scene: Scene)
{
    drive {
        hostSceneButton!!.button.fire()
        val sceneItem = hostSceneButton!!.getSceneItemById(scene.id)!!
        sceneItem.fire()
    }
}

fun LocationDetailsView.removeScene(sceneId: Scene.Id)
{
    drive {
        val hostedSceneItem = hostedScenesList!!.hostedSceneItems.find { it.id == sceneId.toString() }
        clickOn(hostedSceneItem!!.deleteGraphic!!)
    }
}
