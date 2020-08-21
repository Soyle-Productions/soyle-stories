package com.soyle.stories.di.location

import com.soyle.stories.di.DI
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.controllers.DeleteLocationController
import com.soyle.stories.location.controllers.RenameLocationController
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.location.events.DeleteLocationNotifier
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.events.RenameLocationNotifier
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationController
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationControllerImpl
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationNotifier
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocationUseCase
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocationUseCase
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetails
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetailsUseCase
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocationsUseCase
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocation
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocationUseCase
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import com.soyle.stories.location.usecases.renameLocation.RenameLocationUseCase
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.removeSymbolicItem.RemoveSymbolicItemControllerImpl
import com.soyle.stories.theme.renameSymbolicItems.RenameSymbolicItemController

object LocationModule {

	private fun InScope<ProjectScope>.useCases() {
		provide<ListAllLocations> {
			ListAllLocationsUseCase(projectId, get())
		}
		provide<CreateNewLocation> {
			CreateNewLocationUseCase(projectId, get())
		}
		provide<DeleteLocation> {
			DeleteLocationUseCase(get(), get())
		}
		provide<RenameLocation> {
			RenameLocationUseCase(get())
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
		provide(DeleteLocation.OutputPort::class) {
			DeleteLocationNotifier(applicationScope.get()).also {
				get<RemoveSymbolicItemControllerImpl>() listensTo it
			}
		}
		provide(RenameLocation.OutputPort::class) {
			RenameLocationNotifier(applicationScope.get()).also {
				get<RenameSymbolicItemController>() listensTo it
			}
		}
		provide(ReDescribeLocation.OutputPort::class) {
			ReDescribeLocationNotifier(applicationScope.get())
		}

		provide<LocationEvents> {
			object : LocationEvents {
				override val createNewLocation: Notifier<CreateNewLocation.OutputPort> by DI.resolveLater<CreateNewLocationNotifier>(this@provide)
				override val deleteLocation: Notifier<DeleteLocation.OutputPort> by DI.resolveLater<DeleteLocationNotifier>(this@provide)
				override val renameLocation: Notifier<RenameLocation.OutputPort> by DI.resolveLater<RenameLocationNotifier>(this@provide)
				override val reDescribeLocation: Notifier<ReDescribeLocation.OutputPort> by DI.resolveLater<ReDescribeLocationNotifier>(this@provide)
			}
		}
	}

	private fun InScope<ProjectScope>.controllers() {
		provide {
			CreateNewLocationController(get(), get())
		}
		provide {
			DeleteLocationController(get(), get())
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
		DeleteLocationDialogModule
		LocationListModule
		LocationDetailsModule

	}
}