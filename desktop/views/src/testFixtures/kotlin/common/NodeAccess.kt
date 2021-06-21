package com.soyle.stories.desktop.view.common

import javafx.event.EventTarget
import javafx.scene.Node
import org.testfx.api.FxRobot
import tornadofx.CssRule
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class NodeAccess<N : Node>(protected val node: N) : FxRobot() {

    protected fun <Child : Node> mandatoryChild(rule: CssRule): ReadOnlyProperty<NodeAccess<N>, Child> = object : ReadOnlyProperty<NodeAccess<N>, Child> {
        override fun getValue(thisRef: NodeAccess<N>, property: KProperty<*>): Child {
            return from(node).lookup(rule.prefix + rule.name).query<Child>()
        }
    }

    protected fun <Child : Node> temporaryChild(rule: CssRule): ReadOnlyProperty<NodeAccess<N>, Child?> = object : ReadOnlyProperty<NodeAccess<N>, Child?> {
        override fun getValue(thisRef: NodeAccess<N>, property: KProperty<*>): Child? {
            return from(node).lookup(rule.prefix + rule.name).queryAll<Child>().firstOrNull()
        }
    }

    protected fun <Child: Node> children(rule: CssRule): ReadOnlyProperty<NodeAccess<N>, List<Child>> = object : ReadOnlyProperty<NodeAccess<N>, List<Child>> {
        override fun getValue(thisRef: NodeAccess<N>, property: KProperty<*>): List<Child> {
            return from(node).lookup(rule.prefix + rule.name).queryAll<Child>().toList()
        }
    }

}