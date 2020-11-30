package com.soyle.stories.desktop.view.project.workbench

import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialog
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialog
import org.testfx.api.FxRobot
import tornadofx.uiComponent

class WorkbenchDriver(private val workbench: WorkBench) : FxRobot() {

    fun getCreateSceneDialog(): CreateSceneDialog? =
        listWindows().asSequence()
            .mapNotNull { it.scene.root.uiComponent<CreateSceneDialog>() }
            .firstOrNull { it.currentStage?.isShowing == true }

    fun getConfirmDeleteSceneDialog(): DeleteSceneDialog? =
        listWindows().asSequence()
            .mapNotNull { it.scene.root.uiComponent<DeleteSceneDialog>() }
            .firstOrNull { it.currentStage?.isShowing == true }
}