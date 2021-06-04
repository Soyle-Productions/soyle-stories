package com.soyle.stories.di.location

import com.soyle.stories.common.Notifier
import com.soyle.stories.di.DI
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.controllers.RenameLocationController
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.hostedScene.*
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationController
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationControllerImpl
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationNotifier
import com.soyle.stories.location.renameLocation.LocationRenamedNotifier
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver
import com.soyle.stories.location.renameLocation.RenameLocationOutput
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocationUseCase
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetails
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetailsUseCase
import com.soyle.stories.usecase.location.listAllLocations.ListAllLocations
import com.soyle.stories.usecase.location.listAllLocations.ListAllLocationsUseCase
import com.soyle.stories.usecase.location.redescribeLocation.ReDescribeLocation
import com.soyle.stories.usecase.location.redescribeLocation.ReDescribeLocationUseCase
import com.soyle.stories.usecase.location.renameLocation.RenameLocation

object LocationModule {

	private fun InScope<ProjectScope>.useCases() {
		provide<ListAllLocations> {
			ListAllLocationsUseCase(projectId, get())
		}
		provide<CreateNewLocation> {
			CreateNewLocationUseCase(projectId, get())
		}
		provide<GetLocationDetails> {
			GetLocationDetailsUseCase(get())
		}
		provide<ReDescribeLocation> {
			ReDescribeLocationUseCase(get())
		}
	}

	private fun InScope<ProjectScope>.events() {
		provide(CreateNewLocation.OutputPort::class) {
			CreateNewLocationNotifier(applicationScope.get())
		}
		provide(LocationRenamedReceiver::class) {
			LocationRenamedNotifier()
		}
		provide(ReDescribeLocation.OutputPort::class) {
			ReDescribeLocationNotifier(applicationScope.get())
		}

		provide<LocationEvents> {
			object : LocationEvents {
				override val createNewLocation: Notifier<CreateNewLocation.OutputPort> by DI.resolveLater<CreateNewLocationNotifier>(this@provide)
				override val deleteLocation: Notifier<DeletedLocationReceiver> by lazy { this@provide.get<DeletedLocationNotifier>() }
				override val renameLocation: Notifier<RenameLocation.OutputPort> by lazy { this@provide.get() }
				override val reDescribeLocation: Notifier<ReDescribeLocation.OutputPort> by DI.resolveLater<ReDescribeLocationNotifier>(this@provide)
				override val sceneHosted: Notifier<SceneHostedReceiver> by lazy { this@provide.get<SceneHostedNotifier>() }
				override val hostedSceneRenamed: Notifier<HostedSceneRenamedReceiver> by DI.resolveLater<HostedSceneRenamedNotifier>(this@provide)
				override val hostedSceneRemoved: Notifier<HostedSceneRemovedReceiver> by DI.resolveLater<HostedSceneRemovedNotifier>(this@provide)
			}
		}
	}

	private fun InScope<ProjectScope>.controllers() {
		provide {
			CreateNewLocationController(get(), get())
		}
		provide {
			RenameLocationController(get(), get())
		}
		provide<ReDescribeLocationController> {
			ReDescribeLocationControllerImpl(applicationScope.get(), get(), get())
		}
	}

	init {

		scoped<ProjectScope> {
			useCases()
			events()
			controllers()
		}

		CreateLocationDialogModule
		LocationListModule

	}
}