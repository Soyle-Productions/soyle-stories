package com.soyle.stories.scene.listOptionsToReplaceMention


import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.MentionedEntityId
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.usecases.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse

class ListOptionsToReplaceMentionControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val listOptionsToReplaceMention: ListOptionsToReplaceMentionInSceneProse
) : ListOptionsToReplaceMentionController {

    override fun listOptionsToReplaceMention(
        sceneId: Scene.Id,
        entityId: MentionedEntityId<*>,
        output: ListOptionsToReplaceMentionInSceneProse.OutputPort
    ) = threadTransformer.async {
        listOptionsToReplaceMention.invoke(sceneId, entityId, output)
    }

}