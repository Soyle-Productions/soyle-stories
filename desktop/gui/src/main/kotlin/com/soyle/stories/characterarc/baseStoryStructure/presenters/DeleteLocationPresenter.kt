package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation

class DeleteLocationPresenter(
  private val view: View.Nullable<BaseStoryStructureViewModel>
) : DeletedLocationReceiver {

	override suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation) {
		view.updateOrInvalidated {
			withLocations(
			  availableLocations = availableLocations.filterNot { it.id == deletedLocation.location.uuid.toString() }
			)
		}
	}

}