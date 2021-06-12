package com.soyle.stories.desktop.view.testconfig

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.soylestories.Styles
import javafx.scene.Node
import javafx.scene.Parent
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.testfx.api.FxRobot
import tornadofx.FX

const val DESIGN = false

fun FxRobot.verifyDesign(primaryStage: Stage, view: Parent) {
    if (DESIGN) {
        interact {
            ComponentsStyles
            Styles
            val stage =  Stage(StageStyle.DECORATED).apply { initOwner(primaryStage) }
            stage.scene = javafx.scene.Scene(view)
            FX.applyStylesheetsTo(stage.scene)
            stage.showAndWait()
        }
    }
}