package com.soyle.stories.usecase.prose.updateProse

import com.soyle.stories.domain.prose.ContentReplaced
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseContent

interface UpdateProse {

    suspend operator fun invoke(proseId: Prose.Id, content: List<ProseContent>, output: OutputPort)

    class ResponseModel(
        val contentReplaced: ContentReplaced
    )

    interface OutputPort {
        suspend operator fun invoke(response: ResponseModel)
    }

}