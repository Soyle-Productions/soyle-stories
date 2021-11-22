package com.soyle.stories.scene.delete

import com.soyle.stories.common.Confirmation
import com.soyle.stories.scene.PromptChoice

interface DeleteScenePrompt : AutoCloseable {

    suspend fun requestConfirmation(sceneName: String): Confirmation<PromptChoice>?
}