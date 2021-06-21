package com.soyle.stories.desktop.view.common

import javafx.css.Styleable
import javafx.scene.Node
import org.hamcrest.Matcher
import org.testfx.api.FxAssert
import org.testfx.matcher.base.GeneralMatchers
import org.testfx.matcher.base.NodeMatchers
import tornadofx.CssRule
import tornadofx.hasClass
import tornadofx.hasPseudoClass
import java.util.function.Function
import java.util.function.Predicate

class `Node Assertions`(private val node: Node?) {

    companion object {

        fun assertThat(node: Node?): `Node Assertions` = `Node Assertions`(node)
    }

    fun isVisible() = if (node != null) FxAssert.verifyThat(
        node,
        NodeMatchers.isVisible()
    ) else throw AssertionError("Node was expected to be visible, but was null")

    fun isNotVisible() = if (node != null) FxAssert.verifyThat(node, NodeMatchers.isInvisible()) else Unit

}

fun hasPseudoClass(rule: CssRule): Matcher<Styleable>
{
    val descriptionText = "has CSS style \"${rule.name}\""
    return GeneralMatchers.typeSafeMatcher(
        Styleable::class.java, descriptionText,
        Function { styleable: Styleable -> "\"" + styleable.pseudoClassStates + "\"" },
        Predicate { styleable: Styleable -> styleable.hasPseudoClass(rule.name) })
}