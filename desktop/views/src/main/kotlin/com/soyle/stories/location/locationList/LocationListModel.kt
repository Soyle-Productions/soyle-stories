package com.soyle.stories.location.locationList

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.di.resolveLater
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogViewModel
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.toProperty

class LocationListModel : LocationListView, ItemViewModel<LocationListViewModel>() {

	override val scope: ProjectScope = super.scope as ProjectScope

	//val isOpen = find<WorkBenchModel>().select { it.staticTools.find { it.name == "Locations" }?.isOpen.toProperty() }
	val isInvalid: BooleanBinding = itemProperty.isNull
	val selectedItem = SimpleObjectProperty<LocationItemViewModel?>(null)

	val hasLocations = bind { item?.hasLocations?.toProperty() }
	val locations = bindImmutableList(LocationListViewModel::locations)

	val threadTransformer: ThreadTransformer by resolveLater(scope.applicationScope)

	override val viewModel: LocationListViewModel? = item

	override fun update(update: LocationListViewModel?.() -> LocationListViewModel) {
		threadTransformer.gui {
			val selectedItem = this@LocationListModel.selectedItem.value
			item = item.update()
			this@LocationListModel.selectedItem.set(selectedItem)
		}
	}

	override fun updateOrInvalidated(update: LocationListViewModel.() -> LocationListViewModel) {
		threadTransformer.gui {
			val selectedItem = this@LocationListModel.selectedItem.value
			item = item?.update()
			this@LocationListModel.selectedItem.set(selectedItem)
		}
	}

	override fun updateIf(
		condition: LocationListViewModel.() -> Boolean,
		update: LocationListViewModel.() -> LocationListViewModel
	) {
		threadTransformer.gui {
			if (item.condition()) {
				item = item?.update()
			}
		}
	}
}