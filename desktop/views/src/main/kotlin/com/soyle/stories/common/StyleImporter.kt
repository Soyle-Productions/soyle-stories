package com.soyle.stories.common

import javafx.application.Platform
import tornadofx.Stylesheet
import tornadofx.importStylesheet
import tornadofx.runLater
import kotlin.reflect.KClass

abstract class StyleImporter<T : Stylesheet>(stylesheet: KClass<T>) {

    init {
        if (Platform.isFxApplicationThread()) importStylesheet(stylesheet)
        else runLater { importStylesheet(stylesheet) }
    }

}