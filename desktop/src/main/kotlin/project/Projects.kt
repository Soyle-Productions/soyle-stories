package com.soyle.stories.desktop.config.project

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.project.layout.LayoutController
import com.soyle.stories.project.layout.LayoutPresenter
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.scene.characters.tool.SceneCharactersToolScope

object Projects {

    operator fun invoke() {
        scoped<SceneCharactersToolScope> {
            hoist<WorkBench> { projectScope }
        }

    }

}