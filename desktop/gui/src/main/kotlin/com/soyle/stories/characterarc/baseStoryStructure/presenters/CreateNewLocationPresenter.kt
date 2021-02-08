package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation

class CreateNewLocationPresenter(
  private val view: View.Nullable<BaseStoryStructureViewModel>
) : CreateNewLocation.OutputPort {

	override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
		view.updateOrInvalidated {
			withLocations(
			  availableLocations = availableLocations + LocationItemViewModel(response.locationId.toString(), response.locationName)
			)
		}
	}

	override fun receiveCreateNewLocationFailure(failure: Exception) {}

}