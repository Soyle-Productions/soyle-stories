package com.soyle.stories.desktop.view.common

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import org.testfx.api.FxRobot
import tornadofx.CssRule
import tornadofx.Rendered
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class NodeAccess<N : Node>(protected val node: N) : FxRobot() {

    protected fun <Child : Node> mandatoryChild(rule: Rendered, secondaryMatch: (Child) -> Boolean = { true }): ReadOnlyProperty<NodeAccess<N>, Child> =
        node.mandatoryChild(rule, secondaryMatch)

    protected fun <Child : Node> Node.mandatoryChild(rule: Rendered, secondaryMatch: (Child) -> Boolean = { true }): ReadOnlyProperty<NodeAccess<N>, Child> = object : ReadOnlyProperty<NodeAccess<N>, Child> {
        override fun getValue(thisRef: NodeAccess<N>, property: KProperty<*>): Child {
            val matches = from(this@mandatoryChild).lookup(rule.render()).queryAll<Child>()
            return matches.filter(secondaryMatch).singleOrNull() ?:
                error("Multiple children matching [${rule.render()} $secondaryMatch] query: $matches")
        }
    }

    protected fun <Child : Node> temporaryChild(rule: Rendered, secondaryMatch: (Child) -> Boolean = { true }): ReadOnlyProperty<NodeAccess<N>, Child?> =
        node.temporaryChild<Child>(rule, secondaryMatch)

    protected fun <Child : Node> Node?.temporaryChild(rule: Rendered, secondaryMatch: (Child) -> Boolean = { true }): ReadOnlyProperty<NodeAccess<N>, Child?> = object : ReadOnlyProperty<NodeAccess<N>, Child?> {
        override fun getValue(thisRef: NodeAccess<N>, property: KProperty<*>): Child? {
            if (this@temporaryChild == null) return null
            return from(this@temporaryChild).lookup(rule.render()).queryAll<Child>().filter(secondaryMatch).firstOrNull()
        }
    }

    protected fun <Child: Node> children(rule: Rendered): ReadOnlyProperty<NodeAccess<N>, List<Child>> = object : ReadOnlyProperty<NodeAccess<N>, List<Child>> {
        override fun getValue(thisRef: NodeAccess<N>, property: KProperty<*>): List<Child> {
            return from(node).lookup(rule.render()).queryAll<Child>().toList()
        }
    }

    abstract class Factory<Target, N : Node, Accessor : NodeAccess<N>>(private val make: (Target) -> Accessor) {
        fun Target.access(): Accessor = make(this)
        fun Target.access(op: Accessor.() -> Unit) = access().op()
        fun <T> Target.drive(op: Accessor.() -> T): T
        {
            val accessor = access()
            var result: Result<T>? = null
            accessor.interact {
                result = kotlin.runCatching { accessor.op() }
            }
            return result!!.getOrThrow()
        }
    }

}