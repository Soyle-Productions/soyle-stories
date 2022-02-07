package com.soyle.stories.project.closeProject

interface CloseProjectPrompt {

    suspend fun requestConfirmation(): Boolean

}