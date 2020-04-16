package com.soyle.stories.location.locationList

import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.project.WorkBenchModel
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.toProperty

class LocationListModel : LocationListView, ItemViewModel<LocationListViewModel>() {

	//val isOpen = find<WorkBenchModel>().select { it.staticTools.find { it.name == "Locations" }?.isOpen.toProperty() }
	val isInvalid: BooleanBinding = itemProperty.isNull
	val selectedItem = SimpleObjectProperty<LocationItemViewModel?>(null)

	val hasLocations = bind { item?.hasLocations?.toProperty() }
	val locations = bindImmutableList(LocationListViewModel::locations)

	override fun update(update: LocationListViewModel?.() -> LocationListViewModel) {
		ThreadTransformerImpl.gui {
			item = item.update()
		}
	}

	override fun updateOrInvalidated(update: LocationListViewModel.() -> LocationListViewModel) {
		ThreadTransformerImpl.gui {
			item = item?.update()
		}
	}
}