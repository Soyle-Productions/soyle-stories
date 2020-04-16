package com.soyle.stories.location

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.controllers.DeleteLocationController
import com.soyle.stories.location.controllers.RenameLocationController
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.location.events.DeleteLocationNotifier
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.events.RenameLocationNotifier
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocationUseCase
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocationUseCase
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocationsUseCase
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import com.soyle.stories.location.usecases.renameLocation.RenameLocationUseCase
import com.soyle.stories.soylestories.DataComponent
import java.util.*

class LocationComponent(
  projectId: UUID,
  dataComponent: DataComponent
) {

	val listAllLocations: ListAllLocations by lazy {
		ListAllLocationsUseCase(projectId, dataComponent.locationRepository)
	}
	val createNewLocation: CreateNewLocation by lazy {
		CreateNewLocationUseCase(
		  projectId,
		  dataComponent.locationRepository
		)
	}
	val deleteLocation: DeleteLocation by lazy {
		DeleteLocationUseCase(
		  dataComponent.locationRepository,
		  dataComponent.characterArcSectionRepository
		)
	}
	val renameLocation: RenameLocation by lazy {
		RenameLocationUseCase(
		  dataComponent.locationRepository
		)
	}

	private val createNewLocationNotifier by lazy {
		CreateNewLocationNotifier()
	}
	private val deleteLocationNotifier by lazy {
		DeleteLocationNotifier()
	}
	private val renameLocationNotifier by lazy {
		RenameLocationNotifier()
	}

	val locationEvents: LocationEvents = object : LocationEvents {
		override val createNewLocation: Notifier<CreateNewLocation.OutputPort>
			get() = createNewLocationNotifier
		override val deleteLocation: Notifier<DeleteLocation.OutputPort>
			get() = deleteLocationNotifier
		override val renameLocation: Notifier<RenameLocation.OutputPort>
			get() = renameLocationNotifier
	}

	val createNewLocationController by lazy {
		CreateNewLocationController(
		  createNewLocation, createNewLocationNotifier
		)
	}

	val deleteLocationController by lazy {
		DeleteLocationController(
		  deleteLocation, deleteLocationNotifier
		)
	}

	val renameLocationController by lazy {
		RenameLocationController(
		  renameLocation, renameLocationNotifier
		)
	}

}