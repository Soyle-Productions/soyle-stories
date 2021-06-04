package com.soyle.stories.desktop.config.location

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.deleteLocation.DeleteLocationController
import com.soyle.stories.location.deleteLocation.DeleteLocationControllerImpl
import com.soyle.stories.location.deleteLocation.DeleteLocationOutput
import com.soyle.stories.location.hostedScene.listAvailableScenes.ListScenesToHostInLocationController
import com.soyle.stories.location.renameLocation.RenameLocationOutput
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.repositories.CharacterArcRepositoryImpl
import com.soyle.stories.usecase.location.deleteLocation.DeleteLocation
import com.soyle.stories.usecase.location.deleteLocation.DeleteLocationUseCase
import com.soyle.stories.usecase.location.hostedScene.listAvailableScenes.ListScenesToHostInLocation
import com.soyle.stories.usecase.location.hostedScene.listAvailableScenes.ListScenesToHostInLocationUseCase
import com.soyle.stories.usecase.location.renameLocation.RenameLocation
import com.soyle.stories.usecase.location.renameLocation.RenameLocationUseCase

object UseCases {

    init {
        scoped<ProjectScope> {
            deleteLocation()
            renameLocation()
            listScenesToHostInLocation()
        }
    }

    private fun InProjectScope.deleteLocation()
    {
        provide<DeleteLocationController> {
            DeleteLocationControllerImpl(applicationScope.get(), get(), get())
        }
        provide<DeleteLocation> {
            DeleteLocationUseCase(get(), get<CharacterArcRepositoryImpl>(), get())
        }
        provide<DeleteLocation.OutputPort> {
            DeleteLocationOutput(get(), get())
        }
    }

    private fun InProjectScope.renameLocation()
    {
        provide<RenameLocation> {
            RenameLocationUseCase(get(), get(), get())
        }
        provide<RenameLocation.OutputPort> {
            RenameLocationOutput(get(), get(), get())
        }
    }

    private fun InProjectScope.listScenesToHostInLocation()
    {
        provide<ListScenesToHostInLocation> {
            ListScenesToHostInLocationUseCase(get(), get())
        }
        provide { ListScenesToHostInLocationController(applicationScope.get(), get()) }
    }

}