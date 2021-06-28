package com.soyle.stories.scene.sceneEditor


import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope

class SceneEditorState : Model<SceneEditorScope, SceneEditorViewModel>(SceneEditorScope::class) {

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

    val conflict = bind(SceneEditorViewModel::conflict)
    val resolution = bind(SceneEditorViewModel::resolution)

}