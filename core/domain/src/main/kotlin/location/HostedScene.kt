package com.soyle.stories.domain.location

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.scene.Scene

class HostedScene(override val id: Scene.Id, val sceneName: String) : Entity<Scene.Id>