package com.soyle.stories.prose.editProse

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseContent
import kotlinx.coroutines.Job

interface EditProseController {

    fun updateProse(proseId: Prose.Id, content: List<ProseContent>): Job

}