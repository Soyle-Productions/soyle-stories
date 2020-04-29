package com.soyle.stories.di.location

import com.soyle.stories.di.DI
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
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
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetails
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetailsUseCase
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocationsUseCase
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import com.soyle.stories.location.usecases.renameLocation.RenameLocationUseCase
import com.soyle.stories.project.ProjectScope

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
	}

	private fun InScope<ProjectScope>.events() {
		provide(CreateNewLocation.OutputPort::class) {
			CreateNewLocationNotifier()
		}
		provide(DeleteLocation.OutputPort::class) {
			DeleteLocationNotifier()
		}
		provide(RenameLocation.OutputPort::class) {
			RenameLocationNotifier()
		}

		provide<LocationEvents> {
			object : LocationEvents {
				override val createNewLocation: Notifier<CreateNewLocation.OutputPort> by DI.resolveLater<CreateNewLocationNotifier>(this@provide)
				override val deleteLocation: Notifier<DeleteLocation.OutputPort> by DI.resolveLater<DeleteLocationNotifier>(this@provide)
				override val renameLocation: Notifier<RenameLocation.OutputPort> by DI.resolveLater<RenameLocationNotifier>(this@provide)
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