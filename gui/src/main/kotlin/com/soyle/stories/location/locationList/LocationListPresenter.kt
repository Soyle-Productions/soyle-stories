package com.soyle.stories.location.locationList

import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.renameLocation.RenameLocation

class LocationListPresenter(
  private val view: LocationListView,
  locationEvents: LocationEvents
) : ListAllLocations.OutputPort {

	private val subPresenters = listOf(
	  object : CreateNewLocation.OutputPort {
		  override fun receiveCreateNewLocationFailure(failure: LocationException) {}

		  override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
			  view.updateOrInvalidated {
				  LocationListViewModel(
					(locations + LocationItemViewModel(response.locationId.toString(), response.locationName)).sortedBy { it.name }
				  )
			  }
		  }
	  } listensTo locationEvents.createNewLocation,
	  object : DeleteLocation.OutputPort {
		  override fun receiveDeleteLocationFailure(failure: LocationException) { }
		  override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
			  view.updateOrInvalidated {
				  LocationListViewModel(
					locations.filterNot { it.id == response.locationId.toString() }
				  )
			  }
		  }
	  } listensTo locationEvents.deleteLocation,
	  object : RenameLocation.OutputPort {
		  override fun receiveRenameLocationFailure(failure: LocationException) {
		  }

		  override fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
			  view.updateOrInvalidated {
				  LocationListViewModel(
					(locations.filterNot { it.id == response.locationId.toString() } + LocationItemViewModel(response.locationId.toString(), response.newName)).sortedBy { it.name }
				  )
			  }
		  }
	  } listensTo locationEvents.renameLocation
	)

	override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
		view.update {
			LocationListViewModel((response.locations.map(::LocationItemViewModel)).sortedBy { it.name })
		}
	}
}