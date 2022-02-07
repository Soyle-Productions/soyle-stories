package com.soyle.stories.usecase.scene.list

import com.soyle.stories.domain.prose.Prose
import java.util.*

data class SceneItem(val id: UUID, val proseId: Prose.Id, val sceneName: String, val index: Int)