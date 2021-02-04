package com.soyle.stories.scene.usecases.listAllScenes

import com.soyle.stories.entities.Prose
import java.util.*

data class SceneItem(val id: UUID, val proseId: Prose.Id, val sceneName: String, val index: Int)