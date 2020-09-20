package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.ToolScope
import com.soyle.stories.layout.config.dynamic.SceneDetails
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent
import java.util.*

class SceneDetailsScope(
	projectScope: ProjectScope,
	toolId: String,
	type: SceneDetails
) : ToolScope<SceneDetails>(projectScope, toolId, type) {

	val sceneId: UUID
		get() = type.sceneId

}