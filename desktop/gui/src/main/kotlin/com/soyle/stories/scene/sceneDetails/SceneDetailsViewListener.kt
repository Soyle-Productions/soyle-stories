package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.domain.location.Location

interface SceneDetailsViewListener {

	fun getValidState()
	fun linkLocation(locationId: Location.Id)
}