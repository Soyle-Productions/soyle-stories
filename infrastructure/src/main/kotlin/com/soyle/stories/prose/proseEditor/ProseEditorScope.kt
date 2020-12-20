package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.SubProjectScope
import com.soyle.stories.entities.Prose
import com.soyle.stories.project.ProjectScope

class ProseEditorScope(
    projectScope: ProjectScope,
    val proseId: Prose.Id,
    val onLoadMentionQuery: (NonBlankString, OnLoadMentionQueryOutput) -> Unit
) : SubProjectScope(projectScope)