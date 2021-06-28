package com.soyle.stories.scene.listOptionsToReplaceMention

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse
import kotlinx.coroutines.Job

interface ListOptionsToReplaceMentionController {

    fun listOptionsToReplaceMention(
        sceneId: Scene.Id,
        entityId: MentionedEntityId<*>,
        output: ListOptionsToReplaceMentionInSceneProse.OutputPort
    ): Job

}