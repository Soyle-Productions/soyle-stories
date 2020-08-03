package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.baseStoryStructure.presenters.*
import com.soyle.stories.characterarc.changeSectionValue.ChangedCharacterArcSectionValueReceiver
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue

class BaseStoryStructurePresenter(
  private val view: View.Nullable<BaseStoryStructureViewModel>,
  changeThematicSectionValueNotifier: Notifier<ChangeThematicSectionValue.OutputPort>,
  linkLocationToCharacterArcSection: Notifier<LinkLocationToCharacterArcSection.OutputPort>,
  unlinkLocationFromCharacterArcSection: Notifier<UnlinkLocationFromCharacterArcSection.OutputPort>,
  locationEvents: LocationEvents
) : ViewBaseStoryStructure.OutputPort, ListAllLocations.OutputPort, ChangedCharacterArcSectionValueReceiver {

    private val subPresenters = listOf(
      ChangeThematicSectionValuePresenter(view) listensTo changeThematicSectionValueNotifier,
      DeleteLocationPresenter(view) listensTo locationEvents.deleteLocation,
      CreateNewLocationPresenter(view) listensTo locationEvents.createNewLocation,
      LinkLocationToCharacterArcSectionPresenter(view) listensTo linkLocationToCharacterArcSection,
      UnlinkLocationFromCharacterArcSectionPresenter(view) listensTo unlinkLocationFromCharacterArcSection
    )

    override fun receiveViewBaseStoryStructureResponse(response: ViewBaseStoryStructure.ResponseModel) {
        view.update {
            val locationMap = this?.availableLocations?.associateBy { it.id } ?: emptyMap()
            when (this) {
                null -> PartialWithSections(
                  response.sections.map {
                      StoryStructureSectionViewModel(
                        it.templateName,
                        it.arcSectionId.toString(),
                        it.value,
                        it.subSections.map { (subSectionName, value) ->
                            SubSectionViewModel(it.templateName, it.arcSectionId.toString(), subSectionName, value)
                        },
                        null
                      )
                  },
                  response.sections.associate { it.arcSectionId.toString() to it.linkedLocation?.toString() }
                )
                else ->  withSections(
                  response.sections.map {
                      StoryStructureSectionViewModel(
                        it.templateName,
                        it.arcSectionId.toString(),
                        it.value,
                        it.subSections.map { (subSectionName, value) ->
                            SubSectionViewModel(it.templateName, it.arcSectionId.toString(), subSectionName, value)
                        },
                        it.linkedLocation?.let {
                            locationMap[it.toString()]
                        }
                      )
                  }
                )
            }
        }
    }

    override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
        view.update {
            when (this) {
                null -> PartialWithLocations(
                  response.locations.map(::LocationItemViewModel)
                )
                else -> withLocations(
                  response.locations.map(::LocationItemViewModel)
                )
            }
        }
    }

    override suspend fun receiveChangedCharacterArcSectionValue(changedCharacterArcSectionValue: ChangedCharacterArcSectionValue) {
        subPresenters.filterIsInstance<ChangeThematicSectionValuePresenter>().single().receiveChangeThematicSectionValueResponse(
            ChangeThematicSectionValue.ResponseModel(changedCharacterArcSectionValue.arcSectionId, changedCharacterArcSectionValue.newValue)
        )
    }

    override fun receiveViewBaseStoryStructureFailure(failure: Exception) {}
}