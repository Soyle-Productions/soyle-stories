package com.soyle.stories.desktop.config.scene

import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.repositories.SceneRepositoryImpl
import com.soyle.stories.scene.repositories.SceneRepository

object Persistence {

    init {

        scoped<ProjectScope> {

            provide<SceneRepository> { SceneRepositoryImpl() }

        }

    }

}