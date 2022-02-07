package com.soyle.stories.desktop.config.scene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.repositories.SceneRepositoryImpl
import com.soyle.stories.scene.characters.tool.SceneCharactersToolScope
import com.soyle.stories.usecase.scene.SceneRepository

object Persistence {

    init {

        scoped<ProjectScope> {

            provide<SceneRepository> { SceneRepositoryImpl() }

        }

        scoped<SceneCharactersToolScope> {
            hoist<ThreadTransformer> { projectScope.applicationScope }
        }

    }

}