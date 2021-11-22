package com.soyle.stories.scene.create

import com.soyle.stories.domain.validation.NonBlankString

interface CreateScenePrompt : AutoCloseable {
    suspend fun requestSceneName(): NonBlankString?
}