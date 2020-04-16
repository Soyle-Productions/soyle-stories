package com.soyle.stories.location.locationList

import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.di.location.LocationComponent
import com.soyle.stories.project.ProjectScope
import tornadofx.Component
import tornadofx.ScopedInstance

class LocationListComponent : Component(), ScopedInstance {

	override val scope: ProjectScope = super.scope as ProjectScope
	val locationComponent by inject<LocationComponent>()

	val locationListViewListener: LocationListViewListener by lazy {
		LocationListController(
		  ThreadTransformerImpl,
		  locationComponent.listAllLocations,
		  LocationListPresenter(
			find<LocationListModel>(),
			locationComponent.locationEvents
		  ),
		  locationComponent.renameLocationController
		)
	}

}