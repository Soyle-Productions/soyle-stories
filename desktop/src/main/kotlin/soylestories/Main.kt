package com.soyle.stories.desktop.config.soylestories

import com.soyle.stories.Locale
import com.soyle.stories.desktop.config.character.Characters
import com.soyle.stories.desktop.config.location.Locations
import com.soyle.stories.desktop.config.project.Projects
import com.soyle.stories.desktop.config.prose.Prose
import com.soyle.stories.desktop.config.ramifications.Ramifications
import com.soyle.stories.desktop.config.scene.Scenes
import com.soyle.stories.desktop.config.storyevent.StoryEvents
import com.soyle.stories.desktop.config.theme.Themes
import com.soyle.stories.desktop.config.writer.Writers
import com.soyle.stories.desktop.locale.LocaleHolder
import com.soyle.stories.di.configureDI
import com.soyle.stories.di.scoped
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStories
import com.sun.javafx.application.LauncherImpl

fun main(args: Array<String>) {
    SoyleStories.initialization = ::configureModules

    configureLocalization()

    LauncherImpl.launchApplication(SoyleStories::class.java, SoyleStoriesPreLoader::class.java, args)
}

fun configureLocalization() {
    scoped<ApplicationScope> {
        provide(Locale::class, LocaleHolder::class) { LocaleHolder() }
    }
}

fun configureModules() {
    configureDI()
    Projects()
    Ramifications()
    Scenes()
    StoryEvents()
    Prose()
    Themes()
    Characters()
    Locations()
    Writers()
}