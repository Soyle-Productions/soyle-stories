package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.locationList.LiveLocationList
import com.soyle.stories.location.locationList.LocationListListener
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.sceneDetails.includedCharacter.CoveredArcSectionViewModel
import com.soyle.stories.scene.sceneDetails.includedCharacter.IncludedCharacterInScenePresenter
import com.soyle.stories.scene.sceneDetails.includedCharacter.IncludedCharacterInSceneViewModel
import com.soyle.stories.scene.sceneDetails.includedCharacter.PreviousMotivation
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneViewModel
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetails
import com.soyle.stories.scene.usecases.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.scene.usecases.reorderScene.ReorderScene
import java.util.*

class SceneDetailsPresenter(
    sceneId: String,
    private val view: View.Nullable<SceneDetailsViewModel>,
    locationList: LiveLocationList,
    locationLinkedToScene: Notifier<LinkLocationToScene.OutputPort>,
    sceneReordered: Notifier<ReorderScene.OutputPort>
) : GetSceneDetails.OutputPort,
    LocationListListener,
    LinkLocationToScene.OutputPort,
    ReorderScene.OutputPort,
        DeleteScene.OutputPort
{

    private val sceneId = UUID.fromString(sceneId)

    init {
        this listensTo locationList
        this listensTo locationLinkedToScene
        this listensTo sceneReordered
    }

    override fun sceneDetailsRetrieved(response: GetSceneDetails.ResponseModel) {
        view.update {
            val locationId = response.locationId?.toString()
            copyOrDefault(
                invalid = false,
                storyEventId = response.storyEventId.toString(),
                includedCharactersInScene = IncludedCharactersInSceneViewModel(
                    "Characters",
                    response.storyEventId.toString(),
                    "Add Character",
                    "Remove Character",
                    "Position on Character Arc",
                    "Motivation",
                    "Reset Motivation",
                    "When was this last changed?",
                    response.characters.map {
                        IncludedCharacterInSceneViewModel(
                            it.characterId.toString(),
                            it.characterName,
                            it.motivation ?: "",
                            it.motivation != null,
                            it.inheritedMotivation?.let { inherited ->
                                PreviousMotivation(
                                    inherited.motivation,
                                    inherited.sceneId.toString()
                                )
                            },
                            it.coveredArcSections.map {
                                CoveredArcSectionViewModel(
                                    it.arcSectionId.toString(),
                                    it.characterArcId.toString(),
                                    IncludedCharacterInScenePresenter.arcSectionDisplayLabel(
                                        it.characterArcName,
                                        it.arcSectionTemplateName,
                                        it.arcSectionValue,
                                        it.arcSectionTemplateAllowsMultiple
                                    )
                                )
                            },
                            null
                        )
                    },
                    null
                ),
                selectedLocation = locationId?.let {
                    (this?.locations ?: listOf()).find { it.id == locationId }
                        ?: LocationItemViewModel(locationId, "")
                },
                availableLocations = (this?.locations ?: listOf()).filter {
                    it.id != locationId
                }
            )
        }
    }

    override fun receiveLocationListUpdate(locations: List<LocationItem>) {
        view.update {
            val locationId = this?.selectedLocation?.id
            val locationViewModels = locations.map {
                LocationItemViewModel(it.id.toString(), it.locationName)
            }
            copyOrDefault(
                selectedLocation = locationId?.let {
                    locationViewModels.find { it.id == locationId }
                },
                availableLocations = locationViewModels.filter {
                    it.id != locationId
                },
                locations = locationViewModels
            )
        }
    }

    override fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
        if (response.sceneId != sceneId) return
        view.updateOrInvalidated {
            val locationId = response.locationId?.toString()
            copyOrDefault(
                selectedLocation = locationId?.let {
                    locations.find { it.id == locationId }
                },
                availableLocations = locations.filter {
                    it.id != locationId
                }
            )
        }
    }

    override fun sceneReordered(response: ReorderScene.ResponseModel) {
        view.updateOrInvalidated {
            copyOrDefault(invalid = true)
        }
    }

    override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
        view.updateOrInvalidated {
            copyOrDefault(invalid = true)
        }
    }

    private fun SceneDetailsViewModel?.copyOrDefault(
        invalid: Boolean = this?.invalid ?: true,
        storyEventId: String? = this?.storyEventId,
        locationSectionLabel: String = this?.locationSectionLabel ?: "Setting",
        locationDropDownEmptyLabel: String = this?.locationDropDownEmptyLabel ?: "Select Location",
        selectedLocation: LocationItemViewModel? = this?.selectedLocation,
        availableLocations: List<LocationItemViewModel> = this?.availableLocations ?: listOf(),
        includedCharactersInScene: IncludedCharactersInSceneViewModel? = this?.includedCharactersInScene,
        characters: List<CharacterItemViewModel> = this?.characters ?: listOf(),
        locations: List<LocationItemViewModel> = this?.locations ?: listOf()
    ) = SceneDetailsViewModel(
        invalid,
        storyEventId,
        locationSectionLabel,
        locationDropDownEmptyLabel,
        selectedLocation,
        availableLocations,
        includedCharactersInScene,
        characters,
        locations
    )

    override fun failedToGetSceneDetails(failure: Exception) {}
    override fun failedToLinkLocationToScene(failure: Exception) {
        println(failure)
    }

    override fun failedToReorderScene(failure: Exception) {}
    override fun receiveDeleteSceneFailure(failure: SceneException) {}

}