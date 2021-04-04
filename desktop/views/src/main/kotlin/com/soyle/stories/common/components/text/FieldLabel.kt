package com.soyle.stories.common.components.text

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.addChildIfPossible
import tornadofx.addClass
import tornadofx.vbox

class FieldLabel : Label {

    constructor() : super()
    constructor(text: String) : super(text)

    companion object {
        @ViewBuilder
        fun EventTarget.fieldLabel(title: String = "", config: FieldLabel.() -> Unit = {}): FieldLabel =
            FieldLabel(title)
                .also(::addChildIfPossible)
                .also(config)

        @ViewBuilder
        fun EventTarget.fieldLabel(titleProperty: ObservableValue<String>, config: FieldLabel.() -> Unit = {}): FieldLabel =
            FieldLabel().apply { textProperty().bind(titleProperty) }
                .also(::addChildIfPossible)
                .also(config)
    }

    init {
        addClass(TextStyles.fieldLabel)
    }
}