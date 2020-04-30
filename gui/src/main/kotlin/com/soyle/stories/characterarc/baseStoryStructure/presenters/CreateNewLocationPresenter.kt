package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureView
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation

class CreateNewLocationPresenter(
  private val view: BaseStoryStructureView
) : CreateNewLocation.OutputPort {

	override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
		view.update {
			copy(
			  availableLocations = availableLocations + LocationItemViewModel(response.locationId.toString(), response.locationName)
			)
		}
	}

	override fun receiveCreateNewLocationFailure(failure: LocationException) {}

}