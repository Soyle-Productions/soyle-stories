package com.soyle.stories.desktop.config.soylestories

import com.soyle.stories.desktop.config.character.Characters
import com.soyle.stories.desktop.config.theme.Themes
import com.soyle.stories.di.configureDI
import com.soyle.stories.soylestories.SoyleStories
import com.sun.javafx.application.LauncherImpl
import tornadofx.launch

fun main(args: Array<String>) {
    SoyleStories.initialization = ::configureModules
    LauncherImpl.launchApplication(SoyleStories::class.java, SoyleStoriesPreLoader::class.java, args)
}

fun configureModules() {
    configureDI()
    //Scenes()
    Themes()
    Characters()
}