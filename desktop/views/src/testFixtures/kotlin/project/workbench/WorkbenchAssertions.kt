package com.soyle.stories.project.workbench

import com.soyle.stories.desktop.view.project.workbench.WorkbenchDriver
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.project.WorkBench
import org.junit.jupiter.api.Assertions.assertNotNull

class WorkbenchAssertions private constructor(private val driver: WorkbenchDriver) {
    companion object {
        fun assertThat(workBench: WorkBench, assertions: WorkbenchAssertions.() -> Unit) {
            WorkbenchAssertions(WorkbenchDriver(workBench)).assertions()
        }
    }

    fun hasConfirmDeleteSceneDialogOpen(scene: Scene) {
        assertNotNull(driver.getConfirmDeleteSceneDialog()) { "Confirm Delete Scene Dialog is not Open" }
    }

}