package com.soyle.stories.desktop.config.location

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.desktop.config.locale.LocaleHolder
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.location.Location
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogController
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogModel
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogPresenter
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogViewListener
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.location.details.LocationDetailsScope
import com.soyle.stories.location.details.models.LocationDetailsModel
import com.soyle.stories.location.locationDetails.*
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import java.util.*

object Presentation {

    init {
        scoped<ProjectScope> {
            createLocationDialog()
            deleteLocationDialog()

            scoped<LocationDetailsScope> {
                provide<LocationDetailsLocale> { projectScope.applicationScope.get<LocaleHolder>() }
            }
        }
    }

    private fun InProjectScope.createLocationDialog() {
        provide<CreateLocationDialog.Factory> {
            object : CreateLocationDialog.Factory {
                override fun invoke(onCreateLocation: suspend (CreateNewLocation.ResponseModel) -> Unit): CreateLocationDialog =
                    CreateLocationDialog(
                        applicationScope.get(),
                        onCreateLocation,
                        get(),
                        applicationScope.get<LocaleHolder>()
                    )
            }
        }
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
}
