package com.soyle.stories.common

import com.soyle.stories.project.ProjectScope
import kotlin.coroutines.CoroutineContext

interface ComponentContext {
    val gui: CoroutineContext
    val projectScope: ProjectScope
}