package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.ToolScope
import com.soyle.stories.layout.config.dynamic.MoralArgument
import com.soyle.stories.project.ProjectScope

class MoralArgumentScope(
    projectScope: ProjectScope,
    toolId: String,
    type: MoralArgument
) : ToolScope<MoralArgument>(projectScope, toolId, type) {

    val themeId = type.themeId.toString()

}