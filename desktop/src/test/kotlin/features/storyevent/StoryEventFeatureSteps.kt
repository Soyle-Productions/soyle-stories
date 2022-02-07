package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.storyevent.`Story Event Robot`
import com.soyle.stories.desktop.config.features.project.ProjectFeatureSteps

interface StoryEventFeatureSteps : ProjectFeatureSteps {

    val storyEvents: `Story Event Robot`
        get() = `Story Event Robot`(workbench)

}