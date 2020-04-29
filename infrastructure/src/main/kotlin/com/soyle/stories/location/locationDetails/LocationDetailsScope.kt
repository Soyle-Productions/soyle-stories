package com.soyle.stories.location.locationDetails

import com.soyle.stories.di.DI
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.LocationDetailsToolViewModel
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class LocationDetailsScope(val projectScope: ProjectScope, locationDetailsToolViewModel: LocationDetailsToolViewModel) : Scope()
{

	val locationId = locationDetailsToolViewModel.locationId

	init {
		projectScope.addScope(locationId, this)
	}

	fun close() {
		FX.getComponents(this).forEach { (_, it) ->
			if (it is EventTarget) it.removeFromParent()
		}
		deregister()
		DI.deregister(this)
		projectScope.removeScope(locationId, this)
	}

}