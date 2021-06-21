package com.soyle.stories.scene.target

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene

data class SceneTargeted(val sceneId: Scene.Id, val proseId: Prose.Id, val sceneName: String)