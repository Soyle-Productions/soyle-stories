package com.soyle.stories.scene.listOptionsToReplaceMention


import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse

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