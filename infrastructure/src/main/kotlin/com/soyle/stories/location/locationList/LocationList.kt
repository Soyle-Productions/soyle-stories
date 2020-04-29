package com.soyle.stories.location.locationList

import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.resolve
import javafx.scene.layout.Priority
import tornadofx.*

class LocationList : View("Locations") {

	override val root = stackpane {
		hgrow = Priority.SOMETIMES
		vgrow = Priority.ALWAYS
		this += find<PopulatedDisplay>()
		this += find<EmptyDisplay>()
	}

	init {
		val locationListViewListener: LocationListViewListener = resolve()
		val model = find<LocationListModel>()
		model.isInvalid.onChangeWithCurrent {
			if (it != false) {
				locationListViewListener.getValidState()
			}
		}
	}
}