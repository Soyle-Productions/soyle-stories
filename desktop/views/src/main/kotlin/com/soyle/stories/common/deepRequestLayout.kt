package com.soyle.stories.common

import javafx.scene.Node
import javafx.scene.Parent

fun Parent.deepRequestLayout() {
    requestLayout()
    parent?.deepRequestLayout()
}