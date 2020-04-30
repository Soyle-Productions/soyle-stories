package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureView
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation

class DeleteLocationPresenter(
  private val view: BaseStoryStructureView
) : DeleteLocation.OutputPort {

	override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
		view.update {
			copy(
			  availableLocations = availableLocations.filterNot { it.id == response.locationId.toString() }
			)
		}
	}

	override fun receiveDeleteLocationFailure(failure: LocationException) {

	}

}