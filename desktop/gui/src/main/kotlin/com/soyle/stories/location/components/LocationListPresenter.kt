package com.soyle.stories.location.components

import com.soyle.stories.domain.location.Location
import com.soyle.stories.gui.View
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation
import com.soyle.stories.usecase.location.listAllLocations.ListAllLocations

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
			this + LocationItemViewModel(Location.Id(response.locationId), response.locationName)
		}
	}

	override suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation) {
		val locationId = deletedLocation.location.uuid.toString()
		view.updateOrInvalidated {
			this.filterNot { it.id.uuid.toString() == locationId }
		}
	}

	override fun receiveCreateNewLocationFailure(failure: Exception) {}
}