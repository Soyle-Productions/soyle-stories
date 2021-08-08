package com.soyle.stories.desktop.view.common

import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.testconfig.DESIGN
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit

abstract class NodeTest<`Node Under Test` : Node> : FxRobot() {

    init {
        runHeadless()
    }

    protected val primaryStage = FxToolkit.registerPrimaryStage()

    abstract protected val view: `Node Under Test`

    fun showView() {
        val view = view
        interact {
            if (view is Parent) primaryStage.scene = Scene(view)
            else primaryStage.scene = Scene(Pane(view))
            primaryStage.show()
        }
    }

}