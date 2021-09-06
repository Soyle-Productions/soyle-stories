package com.soyle.stories.project.openProject

import kotlinx.coroutines.Job

interface OpenProjectController {
    fun openProject(location: String): Job
    fun forceOpenProject(location: String)
    fun replaceOpenProject(location: String)
}