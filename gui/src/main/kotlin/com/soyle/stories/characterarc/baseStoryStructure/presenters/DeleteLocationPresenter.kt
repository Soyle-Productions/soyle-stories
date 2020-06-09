package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureView
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation

class DeleteLocationPresenter(
  private val view: View.Nullable<BaseStoryStructureViewModel>
) : DeleteLocation.OutputPort {

	override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
		view.updateOrInvalidated {
			withLocations(
			  availableLocations = availableLocations.filterNot { it.id == response.locationId.toString() }
			)
		}
	}

	override fun receiveDeleteLocationFailure(failure: LocationException) {

	}

}