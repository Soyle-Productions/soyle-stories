package com.soyle.stories.scene.sceneEditor


import com.soyle.stories.common.ToolScope
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.config.dynamic.SceneEditor
import com.soyle.stories.project.ProjectScope

class SceneEditorScope(
    projectScope: ProjectScope,
    toolId: String,
    type: SceneEditor
) : ToolScope<SceneEditor>(projectScope, toolId, type) {

    val sceneId: Scene.Id
        get() = type.sceneId

}