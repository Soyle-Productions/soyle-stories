package com.soyle.stories.location.details

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.location.events.HostedSceneRenamed
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.location.details.models.AvailableSceneToHostModel
import com.soyle.stories.location.details.models.HostedSceneItemModel
import com.soyle.stories.location.details.models.LocationDetailsModel
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.hostedScene.HostedSceneRemovedReceiver
import com.soyle.stories.location.hostedScene.HostedSceneRenamedReceiver
import com.soyle.stories.location.hostedScene.SceneHostedReceiver
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetails
import com.soyle.stories.usecase.location.hostedScene.listAvailableScenes.ListScenesToHostInLocation
import com.soyle.stories.usecase.location.redescribeLocation.ReDescribeLocation
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyStringProperty
import tornadofx.*
import java.util.*

class LocationDetailsPresenter : ViewModel(), GetLocationDetails.OutputPort, ReDescribeLocation.OutputPort, ListScenesToHostInLocation.OutputPort,
    SceneHostedReceiver, HostedSceneRenamedReceiver, HostedSceneRemovedReceiver {

    override val scope: LocationDetailsScope = super.scope as LocationDetailsScope

    private val locationId: Location.Id = Location.Id(UUID.fromString(scope.locationId))
    private val locale: LocationDetailsLocale = scope.get()
    private val threadTransformer: ThreadTransformer = scope.projectScope.applicationScope.get()

    val state = objectProperty<LocationDetailsModel>(LocationDetailsModel.Loading)

    private val _toolNameProperty = stringProperty()
    val toolNameProperty: ReadOnlyStringProperty
        get() = _toolNameProperty

    private val descriptionProperty = stringProperty()

    private val availableScenesToHostProperty = ReadOnlyListWrapper<AvailableSceneToHostModel>()

    private val hostedScenesProperty = ReadOnlyListWrapper<HostedSceneItemModel>(observableListOf())

    fun invalidateAvailableScenes() {
        availableScenesToHostProperty.set(null)
    }


    override suspend fun receiveGetLocationDetailsResponse(response: GetLocationDetails.ResponseModel) {
        println("received ${response.hostedScenes}")
        threadTransformer.gui {
            if (state.value is LocationDetailsModel.Loading) state.set(
                LocationDetailsModel.Loaded(
                    descriptionProperty,
                    hostedScenesProperty.readOnlyProperty,
                    availableScenesToHostProperty.readOnlyProperty
                )
            )

            descriptionProperty.set(response.locationDescription)
            hostedScenesProperty.setAll(response.hostedScenes.map {
                HostedSceneItemModel(it.sceneId, stringProperty(it.sceneName))
            })
            _toolNameProperty.bind(locale.locationDetailsToolName(stringProperty(response.locationName)))
        }
    }

    override suspend fun receiveScenesAvailableToHostInLocation(response: ListScenesToHostInLocation.ResponseModel) {
        if (response.locationId == locationId) {
            threadTransformer.gui {
                availableScenesToHostProperty.set(response.availableScenesToHost.map {
                    AvailableSceneToHostModel(it.sceneId, stringProperty(it.sceneName))
                }.toObservable())
            }
        }
    }

    init {
        val locationEvents = scope.projectScope.get<LocationEvents>()
        locationEvents.locationRenamed.addListener {
            if (it.locationId == locationId) {
                _toolNameProperty.bind(locale.locationDetailsToolName(stringProperty(it.newName)))
            }
        }

        this listensTo locationEvents.reDescribeLocation
        this listensTo locationEvents.sceneHosted
        this listensTo locationEvents.hostedSceneRenamed
        this listensTo locationEvents.hostedSceneRemoved
    }

    override fun receiveReDescribeLocationResponse(response: ReDescribeLocation.ResponseModel) {
        if (response.locationId == locationId.uuid) {
            threadTransformer.gui {
                descriptionProperty.set(response.updatedDescription)
            }
        }
    }

    override suspend fun receiveSceneHostedAtLocation(event: SceneHostedAtLocation) {
        if (event.locationId == locationId) {
            threadTransformer.gui {
                hostedScenesProperty.add(HostedSceneItemModel(event.sceneId, stringProperty(event.sceneName)))
            }
        }
    }

    override suspend fun receiveHostedScenesRenamed(events: List<HostedSceneRenamed>) {
        val relevantEvents = events.filter { it.locationId == locationId }
        if (relevantEvents.isNotEmpty()) {
            val renamedHostedScenes = relevantEvents.associateBy { it.sceneId }
            threadTransformer.gui {
                hostedScenesProperty.forEach {
                    val renamedHostedScene = renamedHostedScenes[it.id] ?: return@forEach
                    it.name.set(renamedHostedScene.newName)
                }
            }
        }
    }

    override suspend fun receiveHostedScenesRemoved(events: List<HostedSceneRemoved>) {
        val relevantEvents = events.filter { it.locationId == locationId }
        if (relevantEvents.isNotEmpty()) {
            val removedSceneIds = relevantEvents.asSequence().map { it.sceneId }.toSet()
            threadTransformer.gui {
                hostedScenesProperty.removeIf { it.id in removedSceneIds }
            }
        }
    }

    override fun receiveReDescribeLocationFailure(failure: Exception) = Unit


}