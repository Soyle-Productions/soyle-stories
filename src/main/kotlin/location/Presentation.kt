package com.soyle.stories.desktop.config.location

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogController
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogModel
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogPresenter
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogViewListener
import com.soyle.stories.location.locationDetails.*
import com.soyle.stories.location.renameLocation.LocationRenamedNotifier
import com.soyle.stories.project.ProjectScope

object Presentation {

    init {
        scoped<ProjectScope> {
            deleteLocationDialog()
        }
        locationDetails()
    }

    private fun InProjectScope.deleteLocationDialog() {
        provide<DeleteLocationDialogViewListener> {
            val presenter = DeleteLocationDialogPresenter(
                get<DeleteLocationDialogModel>()
            )

            DeleteLocationDialogController(
                get(),
                presenter
            )
        }
    }

    private fun locationDetails() {
        scoped<LocationDetailsScope> {

            provide<LocationDetailsViewListener> {
                LocationDetailsController(
                    projectScope.applicationScope.get(),
                    locationId,
                    projectScope.get(),
                    LocationDetailsPresenter(
                        locationId,
                        get<LocationDetailsModel>(),
                        projectScope.get(),
                        projectScope.get<LocationRenamedNotifier>()
                    ),
                    projectScope.get()
                )
            }

        }
    }

}