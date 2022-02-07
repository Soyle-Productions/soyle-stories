package com.soyle.stories.common

import javafx.application.Platform
import tornadofx.Stylesheet
import tornadofx.importStylesheet
import tornadofx.runLater
import kotlin.reflect.KClass

inline fun <reified T : Stylesheet> styleImporter() {
    if (Platform.isFxApplicationThread()) importStylesheet(T::class)
    else runLater { importStylesheet(T::class) }
}