package com.soyle.stories.common.builders

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.applyNothing
import com.soyle.stories.common.scopedListener
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.layout.Pane
import tornadofx.add
import tornadofx.removeFromParent
import tornadofx.stringProperty
import javafx.scene.layout.HBox as FX_HBox
import javafx.scene.layout.VBox as FX_VBox

typealias AnyBuilderScope = BuilderScope<*>
interface BuilderScope<T> {
    val target: T
    val children: MutableList<Node>
    fun addChild(child: Node)
}

@JvmInline
value class PaneBuilderScope(override val target: Pane) : BuilderScope<Pane> {
    override fun addChild(child: Node) {
        target.add(child)
    }

    override val children: MutableList<Node>
        get() = target.children
}

inline fun <T: Pane> initPane(
    pane: T,
    configure: NodeConfiguration = Node::applyNothing,
    buildChildren: BuilderScope<Pane>.() -> Unit = BuilderScope<Pane>::applyNothing
): T {
    val scope = PaneBuilderScope(pane.apply(configure))
    scope.buildChildren()
    return pane
}

inline fun HBox(
    configure: NodeConfiguration = Node::applyNothing,
    buildChildren: BuilderScope<Pane>.() -> Unit = BuilderScope<Pane>::applyNothing
): Node = initPane(FX_HBox(), configure, buildChildren)

@ViewBuilder
inline fun AnyBuilderScope.hbox(
    configure: NodeConfiguration = Node::applyNothing,
    buildChildren: BuilderScope<Pane>.() -> Unit = BuilderScope<Pane>::applyNothing
): Node {
    return HBox(configure, buildChildren).also { addChild(it) }
}

inline fun VBox(
    configure: NodeConfiguration = Node::applyNothing,
    buildChildren: BuilderScope<Pane>.() -> Unit = BuilderScope<Pane>::applyNothing
): Node = initPane(FX_VBox(), configure, buildChildren)

@ViewBuilder
inline fun AnyBuilderScope.vbox(
    configure: NodeConfiguration = Node::applyNothing,
    buildChildren: BuilderScope<Pane>.() -> Unit = BuilderScope<Pane>::applyNothing
): Node {
    return VBox(configure, buildChildren).also { addChild(it) }
}

class DynamicContentScope<T>(val scope: BuilderScope<T>, val startIndex: Int): BuilderScope<T> {
    override val target: T
        get() = scope.target

    override val children: MutableList<Node>
        get() = scope.children

    private val addedChildren = mutableListOf<Node>()

    override fun addChild(child: Node) {
        addedChildren.add(child)
        scope.addChild(child)
    }

    fun clearAddedChildren() {
        addedChildren.forEach { it.removeFromParent() }
        addedChildren.clear()
    }
}

@ViewBuilder
inline fun <T, R : Any> BuilderScope<T>.branchContent(
    basedOn: ObservableValue<R>,
    crossinline determine: BuilderScope<T>.(R) -> Unit
) {
    val scope = DynamicContentScope(this, children.size)
    target?.scopedListener(basedOn) {
        scope.clearAddedChildren()
        scope.determine(it!!)
    }
}