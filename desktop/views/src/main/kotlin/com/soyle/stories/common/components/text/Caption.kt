package com.soyle.stories.common.components.text

import com.soyle.stories.common.ViewBuilder
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.Label
import tornadofx.addChildIfPossible
import tornadofx.addClass

class Caption : Label {

    constructor() : super()
    constructor(text: String) : super(text)

    companion object {
        @ViewBuilder
        fun EventTarget.caption(text: String = "", config: Caption.() -> Unit = {}): Caption =
            Caption(text)
                .also(::addChildIfPossible)
                .also(config)

        @ViewBuilder
        fun EventTarget.caption(textProperty: ObservableValue<String>, config: Caption.() -> Unit = {}): Caption =
            Caption().apply { textProperty().bind(textProperty) }
                .also(::addChildIfPossible)
                .also(config)
    }

    init {
        addClass(TextStyles.caption)
    }
}