package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureViewModel
import com.soyle.stories.domain.location.LocationRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver

class LocationRenamedPresenter(
    private val view: View.Nullable<BaseStoryStructureViewModel>
) : LocationRenamedReceiver {

    override suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed) {
        val renamedLocationId = locationRenamed.locationId.uuid.toString()
        view.updateOrInvalidated {
            withLocations(
                availableLocations.map {
                    if (it.id == renamedLocationId) LocationItemViewModel(it.id, locationRenamed.newName)
                    else it
                }
            )
        }
    }
}