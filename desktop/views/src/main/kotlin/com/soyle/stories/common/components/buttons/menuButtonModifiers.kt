package com.soyle.stories.common.components.buttons

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.MenuButton
import tornadofx.addClass
import tornadofx.removeClass
import tornadofx.toggleClass

var MenuButton.hasArrow: Boolean
    get() = hasArrowProperty().value
    set(value) = hasArrowProperty().set(value)

fun MenuButton.hasArrowProperty() = properties.getOrPut("com.soyle.stories.menuButton.hasArrowProperty") {
    val prop = SimpleBooleanProperty(true)
    toggleClass(ButtonStyles.noArrow, prop.not())
    prop
} as BooleanProperty