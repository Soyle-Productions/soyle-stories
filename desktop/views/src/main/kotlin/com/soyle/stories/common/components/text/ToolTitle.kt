package com.soyle.stories.common.components.text

import com.soyle.stories.common.ViewBuilder
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Label
import tornadofx.addChildIfPossible
import tornadofx.addClass

class ToolTitle : Label {

    constructor() : super()
    constructor(text: String) : super(text)
    constructor(text: String, graphic: Node?) : super(text, graphic)

    companion object {
        @ViewBuilder
        fun EventTarget.toolTitle(title: String = "", config: ToolTitle.() -> Unit = {}): ToolTitle =
            ToolTitle().apply { text = title }
                .also(::addChildIfPossible)
                .also(config)

        @ViewBuilder
        fun EventTarget.toolTitle(titleProperty: ObservableValue<String?>, config: ToolTitle.() -> Unit = {}): ToolTitle =
            ToolTitle().apply { textProperty().bind(titleProperty) }
                .also(::addChildIfPossible)
                .also(config)
    }

    init {
        addClass(TextStyles.toolLevelTitle)
    }
}