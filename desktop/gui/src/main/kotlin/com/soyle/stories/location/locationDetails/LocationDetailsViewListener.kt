package com.soyle.stories.location.locationDetails

import com.soyle.stories.domain.scene.Scene

interface LocationDetailsViewListener {

	fun getValidState()
	fun reDescribeLocation(newDescription: String)
	fun getAvailableScenesToHost()
	fun hostScene(sceneId: Scene.Id)
}
