package com.soyle.stories.location.components

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import tornadofx.toObservable

private class LocationListModel(
  private val model: SimpleListProperty<LocationItemViewModel>,
  private val threadTransformer: ThreadTransformer
): View<List<LocationItemViewModel>>,
  ObservableList<LocationItemViewModel> by model
{
	val presenter by lazy {
		LocationListPresenter(this)
	}

	override fun updateOrInvalidated(update: List<LocationItemViewModel>.() -> List<LocationItemViewModel>) {
		threadTransformer.gui { model.set(model.get().update().toObservable()) }
	}
}