package com.soyle.stories.common.components.text

import com.soyle.stories.common.ViewBuilder
import javafx.event.EventTarget
import javafx.scene.control.Label
import tornadofx.addChildIfPossible
import tornadofx.addClass

class ApplicationTitle : Label() {

    companion object {
        @ViewBuilder
        fun EventTarget.applicationTitle(config: ApplicationTitle.() -> Unit = {}): ApplicationTitle =
            ApplicationTitle()
                .also(::addChildIfPossible)
                .also(config)
    }

    init {
        addClass(TextStyles.applicationLevelTitle)
    }
}