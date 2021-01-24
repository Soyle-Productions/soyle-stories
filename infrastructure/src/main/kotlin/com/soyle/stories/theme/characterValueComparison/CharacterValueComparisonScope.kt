package com.soyle.stories.theme.characterValueComparison

import com.soyle.stories.common.ToolScope
import com.soyle.stories.layout.config.dynamic.CharacterValueComparison
import com.soyle.stories.project.ProjectScope

class CharacterValueComparisonScope(
    projectScope: ProjectScope,
    toolId: String,
    type: CharacterValueComparison
) : ToolScope<CharacterValueComparison>(projectScope, toolId, type)