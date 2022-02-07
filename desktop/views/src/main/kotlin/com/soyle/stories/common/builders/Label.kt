package com.soyle.stories.common.builders

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.applyNothing
import com.soyle.stories.common.builders.internal.configureLabeled
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.add
import javafx.scene.control.Label as FXLabel

@JvmName("applyBinding")
inline operator fun <T> ObservableValue<T>.invoke(binding: () -> Property<T>) {
    binding().bind(this)
}
@JvmName("maybeApplyBinding")
inline operator fun <T> ObservableValue<T>?.invoke(binding: () -> Property<T>) {
    this?.invoke(binding)
}

inline fun Label(
    text: ObservableValue<String>? = null,
    graphic: ObservableValue<Node?>? = null,
    labelFor: ObservableValue<Node?>? = null,
    configure: Node.() -> Unit = Node::applyNothing
): Node {
    return FXLabel().apply {
        labelFor { labelForProperty() }

        configureLabeled(
            text,
            graphic,
            configure
        )
    }
}

@ViewBuilder
inline fun AnyBuilderScope.label(
    text: ObservableValue<String>? = null,
    graphic: ObservableValue<Node?>? = null,
    labelFor: ObservableValue<Node?>? = null,
    configure: Node.() -> Unit = Node::applyNothing
): Node {
    return Label(
        text,
        graphic,
        labelFor,
        configure
    ).also {
        addChild(it)
    }
}