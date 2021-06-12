package com.soyle.stories.desktop.config.soylestories

import com.soyle.stories.desktop.config.character.Characters
import com.soyle.stories.desktop.config.locale.LocaleHolder
import com.soyle.stories.desktop.config.location.Locations
import com.soyle.stories.desktop.config.project.Projects
import com.soyle.stories.desktop.config.prose.Prose
import com.soyle.stories.desktop.config.scene.Scenes
import com.soyle.stories.desktop.config.theme.Themes
import com.soyle.stories.desktop.locale.SoyleMessages
import com.soyle.stories.di.DI
import com.soyle.stories.di.configureDI
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStories
import com.sun.javafx.application.LauncherImpl
import javafx.beans.value.ObservableValue
import tornadofx.objectProperty
import java.util.*
import java.util.prefs.Preferences

fun main(args: Array<String>) {
    SoyleStories.initialization = ::configureModules

    DI.register(
        LocaleHolder::class,
        ApplicationScope::class,
        { LocaleHolder(SoyleMessages.getLocale(Locale.getDefault())) },
        true,
        true
    )

    LauncherImpl.launchApplication(SoyleStories::class.java, SoyleStoriesPreLoader::class.java, args)
}

fun configureModules() {
    configureDI()
    Projects()
    Scenes()
    Prose()
    Themes()
    Characters()
    Locations()
}