package com.soyle.stories.location.details

import com.soyle.stories.di.DI
import com.soyle.stories.layout.config.dynamic.LocationDetails
import com.soyle.stories.project.ProjectScope
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class LocationDetailsScope(val projectScope: ProjectScope, tool: LocationDetails) : Scope()
{

	val locationId = tool.locationId.toString()

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