package com.soyle.stories.location.locationList

import com.soyle.stories.common.onChangeWithCurrent
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
		val locationListViewListener = find<LocationListComponent>().locationListViewListener
		val model = find<LocationListModel>()
		model.isInvalid.onChangeWithCurrent {
			if (it != false) {
				locationListViewListener.getValidState()
			}
		}
	}
}