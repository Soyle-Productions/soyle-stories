package com.soyle.stories.common.builders.internal

import com.soyle.stories.common.applyNothing
import com.soyle.stories.common.builders.invoke
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.control.Labeled as FXLabeled


inline fun FXLabeled.configureLabeled(
    text: ObservableValue<String>? = null,
    graphic: ObservableValue<Node?>? = null,
    configure: Node.() -> Unit = Node::applyNothing
) {
    apply {
        text { textProperty() }
        graphic { graphicProperty() }

        configure()
    }
}