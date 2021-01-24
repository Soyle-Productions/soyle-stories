package com.soyle.stories.scene.listOptionsToReplaceMention

import com.soyle.stories.entities.MentionedEntityId
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.usecases.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse
import kotlinx.coroutines.Job

interface ListOptionsToReplaceMentionController {

    fun listOptionsToReplaceMention(
        sceneId: Scene.Id,
        entityId: MentionedEntityId<*>,
        output: ListOptionsToReplaceMentionInSceneProse.OutputPort
    ): Job

}