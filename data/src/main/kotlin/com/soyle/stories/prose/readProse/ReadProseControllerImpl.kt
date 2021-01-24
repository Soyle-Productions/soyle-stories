package com.soyle.stories.prose.readProse

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.usecases.readProse.ReadProse

class ReadProseControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val readProse: ReadProse
) : ReadProseController {
    override fun readProse(proseId: Prose.Id, receiver: ReadProse.OutputPort) {
        threadTransformer.async {
            readProse.invoke(proseId, receiver)
        }
    }
}