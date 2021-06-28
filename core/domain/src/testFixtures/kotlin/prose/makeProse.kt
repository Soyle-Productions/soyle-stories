package com.soyle.stories.domain.prose

import com.soyle.stories.domain.project.Project

fun makeProse(
    id: Prose.Id = Prose.Id(),
    projectId: Project.Id = Project.Id(),
    content: List<ProseContent> = listOf(),
    revision: Long = LongRange(0L, Long.MAX_VALUE).random()
): Prose {
    return Prose.build(
        id,
        projectId,
        content,
        revision
    )
}