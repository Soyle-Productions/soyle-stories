package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.ToolScope
import com.soyle.stories.layout.config.dynamic.SceneDetails
import com.soyle.stories.project.ProjectScope
import java.util.*

class SceneDetailsScope(
	projectScope: ProjectScope,
	toolId: String,
	type: SceneDetails
) : ToolScope<SceneDetails>(projectScope, toolId, type) {

	val sceneId: UUID
		get() = type.sceneId
}