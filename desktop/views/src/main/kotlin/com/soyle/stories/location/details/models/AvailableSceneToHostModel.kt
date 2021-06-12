package com.soyle.stories.location.details.models

import com.soyle.stories.domain.scene.Scene
import javafx.beans.value.ObservableValue

class AvailableSceneToHostModel(
    val sceneId: Scene.Id,
    val name: ObservableValue<String>
)