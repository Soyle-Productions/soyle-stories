package com.soyle.stories.location.components

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.gui.View
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.location.events.DeleteLocationNotifier
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
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

	override fun update(update: List<LocationItemViewModel>.() -> List<LocationItemViewModel>) {
		threadTransformer.gui { model.set(model.get().update().toObservable()) }
	}
}

fun ProjectScope.locationListModel(): ObservableList<LocationItemViewModel>
{
	val model = SimpleListProperty(FXCollections.emptyObservableList<LocationItemViewModel>())
	val threadTransformer = applicationScope.get<ThreadTransformer>()
	val viewImpl = LocationListModel(model, threadTransformer)
	get<CreateNewLocationNotifier>().addListener(viewImpl.presenter)
	get<DeleteLocationNotifier>().addListener(viewImpl.presenter)
	LocationListController(
	  threadTransformer,
	  get(),
	  viewImpl.presenter
	)
	return viewImpl
}