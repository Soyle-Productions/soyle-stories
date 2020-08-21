package com.soyle.stories.project.openProject

interface OpenProjectController {
    fun openProject(location: String)
    fun forceOpenProject(location: String)
    fun replaceOpenProject(location: String)
}