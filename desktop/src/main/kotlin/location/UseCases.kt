package com.soyle.stories.desktop.config.location

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.deleteLocation.DeleteLocationController
import com.soyle.stories.location.deleteLocation.DeleteLocationControllerImpl
import com.soyle.stories.location.deleteLocation.DeleteLocationOutput
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocationUseCase
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.repositories.CharacterArcRepositoryImpl

object UseCases {

    init {
        scoped<ProjectScope> {
            deleteLocation()
        }
    }

    private fun InProjectScope.deleteLocation()
    {
        provide<DeleteLocationController> {
            DeleteLocationControllerImpl(applicationScope.get(), get(), get())
        }
        provide<DeleteLocation> {
            DeleteLocationUseCase(get(), get<CharacterArcRepositoryImpl>())
        }
        provide<DeleteLocation.OutputPort> {
            DeleteLocationOutput(get())
        }
    }

}