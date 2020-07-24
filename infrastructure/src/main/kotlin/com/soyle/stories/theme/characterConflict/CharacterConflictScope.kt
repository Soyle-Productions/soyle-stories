package com.soyle.stories.theme.characterConflict

import com.soyle.stories.common.ToolScope
import com.soyle.stories.layout.config.dynamic.CharacterConflict
import com.soyle.stories.project.ProjectScope
import java.util.*

class CharacterConflictScope(
    projectScope: ProjectScope,
    toolId: String,
    type: CharacterConflict
) : ToolScope<CharacterConflict>(projectScope, toolId, type) {

    val themeId = type.themeId.toString()

}