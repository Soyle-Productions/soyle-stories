package com.soyle.stories.desktop.view.testframework

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.soylestories.Styles
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.FX
import kotlin.reflect.KProperty

abstract class DesignTest : FxRobot() {

    private val primaryStage by lazy { FxToolkit.registerPrimaryStage() }

    abstract val node: Node

    fun verifyDesign(initStage: Stage.() -> Unit = {}) = verifyDesign({ node }, initStage)

    private fun verifyDesign(makeView: () -> Node, initStage: Stage.() -> Unit)
    {
        primaryStage
        interact {
            ComponentsStyles
            Styles
            val view = makeView()
            val stage = if (view.scene?.window as? Stage == null) {
                Stage(StageStyle.DECORATED).apply {
                    initOwner(primaryStage)
                    scene = javafx.scene.Scene(if (view is Parent) view else Pane(view))
                }
            } else view.scene!!.window!! as Stage
            stage.title = "Verify Design"
            stage.initStage()

            FX.applyStylesheetsTo(stage.scene)
            if (stage.isShowing) stage.hide()
            stage.showAndWait()
        }
    }

    inner abstract class Design
    {

        abstract val node: Node

        fun verifyDesign(initStage: Stage.() -> Unit = {}) = verifyDesign({ node }, initStage)

    }

}

