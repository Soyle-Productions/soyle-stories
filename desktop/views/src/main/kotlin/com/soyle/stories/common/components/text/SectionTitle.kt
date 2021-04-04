package com.soyle.stories.common.components.text

import com.soyle.stories.common.ViewBuilder
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.addChildIfPossible
import tornadofx.addClass
import tornadofx.vbox

class SectionTitle : Label() {

    companion object {
        @ViewBuilder
        fun EventTarget.sectionTitle(title: String = "", config: SectionTitle.() -> Unit = {}): SectionTitle =
            SectionTitle().apply { text = title }
                .also(::addChildIfPossible)
                .also(config)

        @ViewBuilder
        fun EventTarget.sectionTitle(titleProperty: ObservableValue<String>, config: SectionTitle.() -> Unit = {}): SectionTitle =
            SectionTitle().apply { textProperty().bind(titleProperty) }
                .also(::addChildIfPossible)
                .also(config)

        @ViewBuilder
        fun EventTarget.section(title: String = "", config: VBox.() -> Unit = {}): VBox =
            vbox {
                addClass(TextStyles.section)
                sectionTitle(title)
                config()
            }

        @ViewBuilder
        fun EventTarget.section(titleProperty: ObservableValue<String>, config: VBox.() -> Unit = {}): VBox =
            vbox {
                addClass(TextStyles.section)
                sectionTitle(titleProperty)
                config()
            }
    }

    init {
        addClass(TextStyles.sectionTitle)
    }
}