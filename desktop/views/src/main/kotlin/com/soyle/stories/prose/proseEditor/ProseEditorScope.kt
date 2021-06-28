package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.SubProjectScope
import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseMention
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope

class ProseEditorScope(
    projectScope: ProjectScope,
    val proseId: Prose.Id,
    val onLoadMentionQuery: (NonBlankString, OnLoadMentionQueryOutput) -> Unit,
    val onUseStoryElement: (ProseMention<*>) -> Unit,
    val onLoadMentionReplacements: (MentionedEntityId<*>, OnLoadMentionReplacementsOutput) -> Unit
) : SubProjectScope(projectScope)