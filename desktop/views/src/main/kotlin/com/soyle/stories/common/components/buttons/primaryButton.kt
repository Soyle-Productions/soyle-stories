package com.soyle.stories.common.components.buttons

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.MenuButton
import tornadofx.*

@ViewBuilder
fun EventTarget.primaryButton(
    text: String = "",
    graphic: Node? = null,
    variant: ButtonVariant = ButtonVariant.Filled,
    op: Button.() -> Unit = {}
) =
    button(text, graphic) {
        addClass(ComponentsStyles.primary)
        addClass(variant.rule)
        op()
    }

@ViewBuilder
fun EventTarget.primaryButton(
    text: ObservableValue<String>,
    graphic: Node? = null,
    variant: ButtonVariant = ButtonVariant.Filled,
    op: Button.() -> Unit = {}
) =
    button(text, graphic) {
        addClass(ComponentsStyles.primary)
        addClass(variant.rule)
        op()
    }

fun PrimaryMenuButton(text: String = "", graphic: Node? = null, variant: ButtonVariant = ButtonVariant.Filled): MenuButton {
    return MenuButton(text, graphic).apply {
        addClass(ComponentsStyles.primary)
        addClass(variant.rule)
    }
}

@ViewBuilder
fun EventTarget.primaryMenuButton(
    text: String = "",
    graphic: Node? = null,
    variant: ButtonVariant = ButtonVariant.Filled,
    op: MenuButton.() -> Unit = {}
) = PrimaryMenuButton(text, graphic, variant).apply {
    this@primaryMenuButton.addChildIfPossible(this)
    op()
}

@ViewBuilder
fun EventTarget.primaryMenuButton(
    text: ObservableValue<String>,
    graphic: Node? = null,
    variant: ButtonVariant = ButtonVariant.Filled,
    op: MenuButton.() -> Unit = {}
) = PrimaryMenuButton(graphic = graphic, variant = variant).apply {
    textProperty().bind(text)
    this@primaryMenuButton.addChildIfPossible(this)
    op()
}