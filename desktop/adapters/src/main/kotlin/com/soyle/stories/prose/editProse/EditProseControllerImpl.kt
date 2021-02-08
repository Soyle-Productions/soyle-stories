package com.soyle.stories.prose.editProse

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseContent
import com.soyle.stories.usecase.prose.updateProse.UpdateProse
import kotlinx.coroutines.Job

class EditProseControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val updateProse: UpdateProse,
    private val updateProseOutput: UpdateProse.OutputPort
) : EditProseController {

    override fun updateProse(proseId: Prose.Id, content: List<ProseContent>): Job {
        return threadTransformer.async {
            updateProse.invoke(proseId, content, updateProseOutput)
        }
    }

}