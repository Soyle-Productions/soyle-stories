package com.soyle.stories.location.components

import com.soyle.stories.gui.View
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

class LocationListPresenter(
  private val view: View<List<LocationItemViewModel>>
) : ListAllLocations.OutputPort, CreateNewLocation.OutputPort, DeleteLocation.OutputPort
{

	override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
		view.update {
			response.locations.map {
				LocationItemViewModel(it)
			}
		}
	}

	override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
		view.update {
			this + LocationItemViewModel(response.locationId.toString(), response.locationName)
		}
	}

	override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
		val locationId = response.locationId.toString()
		view.update {
			this.filterNot { it.id == locationId }
		}
	}

	override fun receiveCreateNewLocationFailure(failure: LocationException) {}
	override fun receiveDeleteLocationFailure(failure: LocationException) {}
}