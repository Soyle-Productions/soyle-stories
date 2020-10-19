package com.soyle.stories.desktop.config.soylestories

import com.soyle.stories.desktop.config.scene.Scenes
import com.soyle.stories.di.configureDI
import com.soyle.stories.soylestories.SoyleStories
import tornadofx.launch

fun main(args: Array<String>) {
    configureModules()
    launch<SoyleStories>()
}

fun configureModules() {
    configureDI()
    //Scenes()
}