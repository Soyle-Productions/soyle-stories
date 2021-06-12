package com.soyle.stories.location.details

import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.details.components.LocationDetailsRoot
import com.soyle.stories.location.hostedScene.listAvailableScenes.ListScenesToHostInLocationController
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationController
import com.soyle.stories.scene.createSceneDialog.createSceneDialog
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneController
import javafx.scene.Parent
import tornadofx.View
import java.util.*

class LocationDetailsView : View() {

    override val scope: LocationDetailsScope = super.scope as LocationDetailsScope

    private val locationId = Location.Id(UUID.fromString(scope.locationId))

    private val locale: LocationDetailsLocale by resolveLater()
    private val presenter = resolve<LocationDetailsPresenter>()

    private val actions = object : LocationDetailsActions {
        override fun createSceneToHost() {
            createSceneDialog(scope.projectScope, onSceneCreated = ::hostScene)
        }

        override fun hostScene(sceneId: Scene.Id) {
            scope.projectScope.get<LinkLocationToSceneController>().linkLocationToScene(sceneId, locationId)
        }

        override fun loadAvailableScenes() {
            presenter.invalidateAvailableScenes()
            scope.projectScope.get<ListScenesToHostInLocationController>().listScenesToHostInLocation(locationId, presenter)
        }

        override fun reDescribeLocation(description: String) {
            scope.projectScope.get<ReDescribeLocationController>().reDescribeLocation(locationId.uuid.toString(), description)
        }

        override fun removeScene(sceneId: Scene.Id) {
            scope.projectScope.get<RemoveLocationFromSceneController>().removeLocation(sceneId, locationId)
        }
    }

    override val root: Parent = LocationDetailsRoot(presenter.state, actions, locale)

    init {
        titleProperty.bind(presenter.toolNameProperty)
        scope.projectScope.get<GetLocationDetailsController>().getLocationDetails(locationId, presenter)
    }

}