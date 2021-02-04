package com.soyle.stories.location.components

import com.soyle.stories.gui.View
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.deleteLocation.DeletedLocation
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

class LocationListPresenter(
  private val view: View<List<LocationItemViewModel>>
) : ListAllLocations.OutputPort, CreateNewLocation.OutputPort, DeletedLocationReceiver
{

	override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
		view.updateOrInvalidated {
			response.locations.map {
				LocationItemViewModel(it)
			}
		}
	}

	override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
		view.updateOrInvalidated {
			this + LocationItemViewModel(response.locationId.toString(), response.locationName)
		}
	}

	override suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation) {
		val locationId = deletedLocation.location.uuid.toString()
		view.updateOrInvalidated {
			this.filterNot { it.id == locationId }
		}
	}

	override fun receiveCreateNewLocationFailure(failure: LocationException) {}
}