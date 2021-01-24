package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.ToolScope
import com.soyle.stories.layout.config.dynamic.BaseStoryStructure
import com.soyle.stories.project.ProjectScope

class BaseStoryStructureScope(
  projectScope: ProjectScope,
  toolId: String,
  type: BaseStoryStructure
) : ToolScope<BaseStoryStructure>(
  projectScope, toolId, type
)