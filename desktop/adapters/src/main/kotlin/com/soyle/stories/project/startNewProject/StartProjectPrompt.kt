package com.soyle.stories.project.startNewProject

import com.soyle.stories.domain.validation.NonBlankString

interface StartProjectPrompt : AutoCloseable {

    suspend fun requestDirectory(previousAttempt: String?, errorMessage: String?): String?
    suspend fun requestProjectName(previousAttempt: String? = null, errorMessage: String? = null): NonBlankString?

}