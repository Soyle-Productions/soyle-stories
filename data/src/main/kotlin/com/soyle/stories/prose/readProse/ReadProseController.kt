package com.soyle.stories.prose.readProse

import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.usecases.readProse.ReadProse

interface ReadProseController {
    fun readProse(proseId: Prose.Id, receiver: ReadProse.OutputPort)
}