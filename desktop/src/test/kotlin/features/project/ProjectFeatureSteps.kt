package com.soyle.stories.desktop.config.features.project

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.project.WorkBench
import io.cucumber.java8.En

interface ProjectFeatureSteps : En {

    val workbench: WorkBench
        get() = soyleStories.getAnyOpenWorkbenchOrError()

}