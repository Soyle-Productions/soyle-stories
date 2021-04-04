package com.soyle.stories.common.components.text

import com.soyle.stories.common.ViewBuilder
import javafx.event.EventTarget
import javafx.scene.control.Label
import tornadofx.addChildIfPossible
import tornadofx.addClass

class WindowTitle : Label() {

    companion object {
        @ViewBuilder
        fun EventTarget.windowTitle(config: WindowTitle.() -> Unit = {}): WindowTitle =
            WindowTitle()
                .also(::addChildIfPossible)
                .also(config)
    }

    init {
        addClass(TextStyles.windowLevelTitle)
    }
}