package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.project.WorkBench
import com.soyle.stories.soylestories.SoyleStories
import javafx.scene.layout.BorderPane
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.*
import kotlin.NoSuchElementException

fun SoyleStories.getWorkbenchForProjectOrError(projectId: UUID): WorkBench =
    getWorkbenchForProject(projectId) ?: throw NoSuchElementException("No workbench found for project $projectId in $this")

fun SoyleStories.getWorkbenchForProject(projectId: UUID): WorkBench? = projectViews
    .find { it.scope.projectId == projectId }
    ?.also {
        tailrec suspend fun checkCenter() {
            if ((it.root as? BorderPane)?.center != null) return
            delay(10)
            return checkCenter()
        }
        runBlocking {
            withTimeout(2000) {
                checkCenter()
            }
        }
    }