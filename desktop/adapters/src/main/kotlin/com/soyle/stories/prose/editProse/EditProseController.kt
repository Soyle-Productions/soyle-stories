package com.soyle.stories.prose.editProse

import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseContent
import kotlinx.coroutines.Job

interface EditProseController {

    fun updateProse(proseId: Prose.Id, content: List<ProseContent>): Job

}