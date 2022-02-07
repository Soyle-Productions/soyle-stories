package com.soyle.stories.usecase.scene.common

import com.soyle.stories.domain.scene.Scene
import java.util.*


class AffectedScene(val sceneId: Scene.Id, val sceneName: String, val characters: List<AffectedCharacter>)