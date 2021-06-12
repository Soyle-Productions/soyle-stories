package com.soyle.stories.desktop.view.common

import javafx.scene.Node
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers

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