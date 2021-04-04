package com.soyle.stories.common.components.text

import com.soyle.stories.common.ViewBuilder
import javafx.event.EventTarget
import javafx.scene.control.Label
import tornadofx.addChildIfPossible
import tornadofx.addClass

class ProjectTitle : Label() {

    companion object {
        @ViewBuilder
        fun EventTarget.projectTitle(config: ProjectTitle.() -> Unit = {}): ProjectTitle =
            ProjectTitle()
                .also(::addChildIfPossible)
                .also(config)
    }

    init {
        addClass(TextStyles.projectLevelTitle)
    }
}