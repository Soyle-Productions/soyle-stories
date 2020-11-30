package com.soyle.stories.desktop.view.project.workbench

import com.soyle.stories.project.WorkBench
import org.junit.jupiter.api.Assertions.assertNotNull

class WorkbenchAssertions private constructor(private val driver: WorkbenchDriver) {
    companion object {
        fun assertThat(workBench: WorkBench, assertions: WorkbenchAssertions.() -> Unit) {
            WorkbenchAssertions(WorkbenchDriver(workBench)).assertions()
        }
    }

    fun hasConfirmDeleteSceneDialogOpen() {
        assertNotNull(driver.getConfirmDeleteSceneDialog()) { "Confirm Delete Scene Dialog is not Open" }
    }

}