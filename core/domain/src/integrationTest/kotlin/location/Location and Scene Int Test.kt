package com.soyle.stories.domain.location

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.makeScene
import org.junit.jupiter.api.Test

class `Location and Scene Int Test` {

    private val location = makeLocation()
    private val scene = makeScene()

    @Test
    fun `Scene Hosted Event can be used to Use Location`() {
        val (_, sceneHosted) = location.withSceneHosted(scene.id, scene.name.value) as Updated
        val (updatedScene) = scene.withLocationLinked(
            sceneHosted.locationId,
            // assuming location loaded from external source
            location.name.value
        )
        updatedScene.settings.containsEntityWithId(location.id).mustEqual(true)
    }

}