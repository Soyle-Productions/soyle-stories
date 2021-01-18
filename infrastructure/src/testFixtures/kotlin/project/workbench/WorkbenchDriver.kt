package com.soyle.stories.desktop.view.project.workbench

import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialog
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialog
import org.testfx.api.FxRobot
import tornadofx.UIComponent
import tornadofx.uiComponent

class WorkbenchDriver(private val workbench: WorkBench) : FxRobot() {

    fun getCreateSceneDialog(): CreateSceneDialog? = getOpenDialog()

    fun getConfirmDeleteSceneDialog(): DeleteSceneDialog? = getOpenDialog()
}

inline fun <reified T : UIComponent> FxRobot.getOpenDialog(): T? =
    listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<T>() }
        .firstOrNull { it.currentStage?.isShowing == true }