package com.soyle.stories.project.startNewProject

import com.soyle.stories.domain.validation.NonBlankString

interface StartProjectController {

    fun startProject(directory: String, name: NonBlankString)

}