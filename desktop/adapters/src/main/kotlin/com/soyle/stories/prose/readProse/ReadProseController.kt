package com.soyle.stories.prose.readProse

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.usecase.prose.readProse.ReadProse

interface ReadProseController {
    fun readProse(proseId: Prose.Id, receiver: ReadProse.OutputPort)
}