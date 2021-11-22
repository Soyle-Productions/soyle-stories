package com.soyle.stories.scene.reorder

import com.soyle.stories.common.Confirmation
import com.soyle.stories.scene.PromptChoice

interface ReorderScenePrompt : AutoCloseable {
    suspend fun requestConfirmation(sceneName: String): Confirmation<PromptChoice>?
}