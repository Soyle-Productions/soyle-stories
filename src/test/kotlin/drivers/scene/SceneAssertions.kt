package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories

object SceneAssertions {

    fun assertSceneExistsWithName(sceneName: String)
    {
        SceneDriver(soyleStories.getAnyOpenWorkbenchOrError()).getSceneByNameOrError(sceneName)
    }

}