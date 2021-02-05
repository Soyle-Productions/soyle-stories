package com.soyle.stories.prose.editProse

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseContent
import com.soyle.stories.prose.usecases.updateProse.UpdateProse
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