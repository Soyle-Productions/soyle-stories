package com.soyle.stories.project.startNewProject

import com.soyle.stories.domain.validation.NonBlankString
import kotlinx.coroutines.Job

interface StartProjectController {

    fun startProject(directory: String, name: NonBlankString): Job

}