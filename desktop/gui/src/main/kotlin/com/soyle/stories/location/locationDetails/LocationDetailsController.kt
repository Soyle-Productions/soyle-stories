package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.hostedScene.hostScene.HostSceneController
import com.soyle.stories.location.hostedScene.listAvailableScenes.ListScenesToHostInLocationController
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetails

class LocationDetailsController private constructor(
	private val threadTransformer: ThreadTransformer,
	private val locationId: Location.Id,
	private val getLocationDetails: GetLocationDetails,
	private val reDescribeLocationController: ReDescribeLocationController,
	private val listScenesToHostInLocationController: ListScenesToHostInLocationController,
	private val hostSceneController: HostSceneController,
	private val presenter: LocationDetailsPresenter
) : LocationDetailsViewListener {

	constructor(
		locationId: Location.Id,
		threadTransformer: ThreadTransformer,
		getLocationDetails: GetLocationDetails,
		reDescribeLocationController: ReDescribeLocationController,
		listScenesToHostInLocationController: ListScenesToHostInLocationController,
		hostSceneController: HostSceneController,
		view: LocationDetailsViewModel,
		locationEvents: LocationEvents
	) : this(
		threadTransformer,
		locationId,
		getLocationDetails,
		reDescribeLocationController,
		listScenesToHostInLocationController,
		hostSceneController,
		LocationDetailsPresenter(
			locationId,
			view,
			locationEvents
		)
	)

	override fun getValidState() {
		threadTransformer.async {
			getLocationDetails.invoke(locationId.uuid, presenter)
		}
	}

	override fun reDescribeLocation(newDescription: String) {
		reDescribeLocationController.reDescribeLocation(locationId.uuid.toString(), newDescription)
	}

	override fun getAvailableScenesToHost() {
		listScenesToHostInLocationController.listScenesToHostInLocation(locationId, presenter)
	}

	override fun hostScene(sceneId: Scene.Id) {
		hostSceneController.linkLocationToScene(sceneId, locationId)
	}
}
