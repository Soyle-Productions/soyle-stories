package com.soyle.stories.scene.create

import com.soyle.stories.domain.validation.NonBlankString

interface CreateScenePrompt {
    suspend fun requestSceneName(): NonBlankString?
    suspend fun close()
}