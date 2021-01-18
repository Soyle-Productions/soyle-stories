package com.soyle.stories.prose.usecases.updateProse

import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseContent
import com.soyle.stories.prose.ContentReplaced

interface UpdateProse {

    suspend operator fun invoke(proseId: Prose.Id, content: List<ProseContent>, output: OutputPort)

    class ResponseModel(
        val contentReplaced: ContentReplaced
    )

    interface OutputPort {
        suspend operator fun invoke(response: ResponseModel)
    }

}