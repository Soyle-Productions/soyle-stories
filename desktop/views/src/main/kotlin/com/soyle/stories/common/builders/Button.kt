package com.soyle.stories.common.builders

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.applyNothing
import com.soyle.stories.common.builders.internal.configureLabeled
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.add
import javafx.scene.control.Button as FXButton


inline fun Button(
    text: ObservableValue<String>? = null,
    graphic: ObservableValue<Node?>? = null,
    isDefaultButton: ObservableValue<Boolean>? = null,
    isCancelButton: ObservableValue<Boolean>? = null,
    configure: NodeConfiguration = Node::applyNothing
): Node {
    return FXButton().apply {
        text { textProperty() }
        graphic { graphicProperty() }
        isDefaultButton { defaultButtonProperty() }
        isCancelButton { cancelButtonProperty() }

        configureLabeled(
            text,
            graphic,
            configure
        )
    }
}

@ViewBuilder
inline fun AnyBuilderScope.button(
    text: ObservableValue<String>? = null,
    graphic: ObservableValue<Node?>? = null,
    isDefaultButton: ObservableValue<Boolean>? = null,
    isCancelButton: ObservableValue<Boolean>? = null,
    configure: NodeConfiguration = Node::applyNothing
): Node {
    return Button(
        text,
        graphic,
        isDefaultButton,
        isCancelButton,
        configure
    ).also {
        addChild(it)
    }
}