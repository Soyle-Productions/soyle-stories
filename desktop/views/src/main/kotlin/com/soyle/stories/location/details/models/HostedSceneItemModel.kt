package com.soyle.stories.location.details.models

import com.soyle.stories.domain.scene.Scene
import javafx.beans.property.StringProperty

class HostedSceneItemModel(
    val id: Scene.Id,
    val name: StringProperty
)