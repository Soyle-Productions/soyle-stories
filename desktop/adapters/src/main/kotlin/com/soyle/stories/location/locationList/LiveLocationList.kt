package com.soyle.stories.location.locationList

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.LocationRenamed
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation
import com.soyle.stories.usecase.location.listAllLocations.ListAllLocations
import com.soyle.stories.usecase.location.listAllLocations.LocationItem

class LiveLocationList(
    private val threadTransformer: ThreadTransformer,
    listAllLocations: ListAllLocations,
    createNewLocationNotifier: Notifier<CreateNewLocation.OutputPort>,
    deleteLocationNotifier: Notifier<DeletedLocationReceiver>,
    locationRenamedNotifier: Notifier<LocationRenamedReceiver>
) : Notifier<LocationListListener>() {

    private val loader = lazy {
        threadTransformer.async {
            listAllLocations.invoke(outputs)
            createNewLocationNotifier.addListener(outputs)
            locationRenamedNotifier.addListener(outputs)
            deleteLocationNotifier.addListener(outputs)
        }
    }
    private var locations: List<LocationItem>? = null

    private val outputs = object :
        ListAllLocations.OutputPort,
        CreateNewLocation.OutputPort,
        LocationRenamedReceiver,
        DeletedLocationReceiver {
        override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
            val locations = response.locations
            this@LiveLocationList.locations = locations
            threadTransformer.async {
                notifyAll { it.receiveLocationListUpdate(locations) }
            }
        }

        override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
            val locations = locations!! + LocationItem(response.locationId, response.locationName)
            this@LiveLocationList.locations = locations
            threadTransformer.async {
                notifyAll { it.receiveLocationListUpdate(locations) }
            }
        }

        override suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed) {
            val locations = locations!!.filterNot { it.id == locationRenamed.locationId.uuid } + LocationItem(
                locationRenamed.locationId.uuid,
                locationRenamed.newName
            )
            this@LiveLocationList.locations = locations
            threadTransformer.async {
                notifyAll { it.receiveLocationListUpdate(locations) }
            }
        }

        override suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation) {
            val locations = locations!!.filterNot { it.id == deletedLocation.location.uuid }
            this@LiveLocationList.locations = locations
            threadTransformer.async {
                notifyAll { it.receiveLocationListUpdate(locations) }
            }
        }

        override fun receiveCreateNewLocationFailure(failure: Exception) {}
    }

    override fun addListener(listener: LocationListListener) {
        super.addListener(listener)
        val locations = locations
        if (locations == null) {
            synchronized(this) {
                if (!loader.isInitialized()) loader.value
            }
        } else {
            listener.receiveLocationListUpdate(locations)
        }
    }

}