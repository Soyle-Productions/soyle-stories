package com.soyle.stories.location.locationList

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
import com.soyle.stories.location.usecases.renameLocation.RenameLocation

class LiveLocationList(
  threadTransformer: ThreadTransformer,
  listAllLocations: ListAllLocations,
  createNewLocationNotifier: Notifier<CreateNewLocation.OutputPort>,
  deleteLocationNotifier: Notifier<DeleteLocation.OutputPort>,
  renameLocationNotifier: Notifier<RenameLocation.OutputPort>
) : Notifier<LocationListListener>() {

	private val loader = lazy {
		threadTransformer.async {
			listAllLocations.invoke(outputs)
			createNewLocationNotifier.addListener(outputs)
			renameLocationNotifier.addListener(outputs)
			deleteLocationNotifier.addListener(outputs)
		}
	}
	private var locations: List<LocationItem>? = null

	private val outputs = object :
	  ListAllLocations.OutputPort,
	  CreateNewLocation.OutputPort,
	  RenameLocation.OutputPort,
	  DeleteLocation.OutputPort {
		override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
			val locations = response.locations
			this@LiveLocationList.locations = locations
			notifyAll { it.receiveLocationListUpdate(locations) }
		}

		override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
			val locations = locations!! + LocationItem(response.locationId, response.locationName)
			this@LiveLocationList.locations = locations
			notifyAll { it.receiveLocationListUpdate(locations) }
		}

		override fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
			val locations = locations!!.filterNot { it.id == response.locationId } + LocationItem(response.locationId, response.newName)
			this@LiveLocationList.locations = locations
			notifyAll { it.receiveLocationListUpdate(locations) }
		}

		override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
			val locations = locations!!.filterNot { it.id == response.locationId }
			this@LiveLocationList.locations = locations
			notifyAll { it.receiveLocationListUpdate(locations) }
		}

		override fun receiveCreateNewLocationFailure(failure: LocationException) {}
		override fun receiveDeleteLocationFailure(failure: LocationException) {}
		override fun receiveRenameLocationFailure(failure: LocationException) {}
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