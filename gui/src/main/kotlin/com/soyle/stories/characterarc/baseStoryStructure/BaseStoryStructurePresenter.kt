package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.baseStoryStructure.presenters.*
import com.soyle.stories.characterarc.eventbus.CharacterArcEvents
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

class BaseStoryStructurePresenter(
  private val view: BaseStoryStructureView,
  characterArcEvents: CharacterArcEvents,
  locationEvents: LocationEvents
) : ViewBaseStoryStructure.OutputPort, ListAllLocations.OutputPort {

    private val subPresenters = listOf(
      ChangeThematicSectionValuePresenter(view) listensTo characterArcEvents.changeThematicSectionValue,
      DeleteLocationPresenter(view) listensTo locationEvents.deleteLocation,
      CreateNewLocationPresenter(view) listensTo locationEvents.createNewLocation,
      LinkLocationToCharacterArcSectionPresenter(view) listensTo characterArcEvents.linkLocationToCharacterArcSection,
      UnlinkLocationFromCharacterArcSectionPresenter(view) listensTo characterArcEvents.unlinkLocationFromCharacterArcSection
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

    override fun receiveViewBaseStoryStructureFailure(failure: Exception) {}
}