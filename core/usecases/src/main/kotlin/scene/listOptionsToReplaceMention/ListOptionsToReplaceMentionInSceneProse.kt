package com.soyle.stories.usecase.scene.listOptionsToReplaceMention

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.scene.Scene

interface ListOptionsToReplaceMentionInSceneProse {

    suspend operator fun invoke(sceneId: Scene.Id, entityId: MentionedEntityId<*>, output: OutputPort)

    class ResponseModel<Id : Any> (
        val entityIdToReplace: MentionedEntityId<Id>,
        val options: List<MentionOption<Id>>
    )

    data class MentionOption<Id: Any>(val entityId: MentionedEntityId<Id>, val name: String, val parentName: String?)

    interface OutputPort {
        suspend fun receiveOptionsToReplaceMention(response: ResponseModel<*>)
    }

}