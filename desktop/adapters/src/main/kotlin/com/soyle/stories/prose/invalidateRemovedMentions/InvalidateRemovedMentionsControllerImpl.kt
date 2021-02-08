package com.soyle.stories.prose.invalidateRemovedMentions

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentions

class InvalidateRemovedMentionsControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val detectInvalidatedMentions: DetectInvalidatedMentions,
    private val detectInvalidatedMentionsOutput: DetectInvalidatedMentions.OutputPort
) : InvalidateRemovedMentionsController {

    override fun invalidateRemovedMentions(prose: Prose.Id) {
        threadTransformer.async {
            detectInvalidatedMentions.invoke(prose, detectInvalidatedMentionsOutput)
        }
    }

}