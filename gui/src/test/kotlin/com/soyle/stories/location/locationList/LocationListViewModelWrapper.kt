package com.soyle.stories.location.locationList

import com.soyle.stories.location.items.LocationItemViewModel

class LocationListViewModelWrapper : LocationListView {

	private var viewModel: LocationListViewModel? = null

	val isInEmptyState: Boolean
		get() = ! viewModel!!.hasLocations
	val locations: List<LocationItemViewModel>
		get() = viewModel!!.locations

	override fun update(update: LocationListViewModel?.() -> LocationListViewModel) {
		viewModel = viewModel.update()
	}

	override fun updateOrInvalidated(update: LocationListViewModel.() -> LocationListViewModel) {
		viewModel = viewModel?.update()
	}
}