package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.project.WorkBench
import com.soyle.stories.soylestories.SoyleStories
import java.util.*
import kotlin.NoSuchElementException

fun SoyleStories.getWorkbenchForProjectOrError(projectId: UUID): WorkBench =
    getWorkbenchForProject(projectId) ?: throw NoSuchElementException("No workbench found for project $projectId in $this")

fun SoyleStories.getWorkbenchForProject(projectId: UUID): WorkBench? = projectViews.find { it.scope.projectId == projectId }