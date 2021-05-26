package com.soyle.stories.domain.scene

import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import org.junit.jupiter.api.Test

class `Scene and Location Int Test` {

    private val location = makeLocation()
    private val scene = makeScene()

    @Test
    fun `Location Used Event can be used to Host Scene`() {
        val (_, locationUsed) = scene.withLocationLinked(location.id, location.name.value) as Updated
        val (updatedLocation) = location.withSceneHosted(locationUsed.sceneId, scene.name.value)

        updatedLocation.hostedScenes.containsEntityWithId(scene.id).mustEqual(true)
    }

    @Test
    fun `Scene Renamed Event can be used to Rename Hosted Scene`() {
        val newName = sceneName()
        val (_, sceneRenamed) = scene.withName(newName) as Updated
        val (updatedLocation) = location.withSceneHosted(scene.id, scene.name.value).location
            .withHostedScene(sceneRenamed.sceneId).renamed(to = sceneRenamed.sceneName)

        updatedLocation.hostedScenes.getEntityById(scene.id)!!.sceneName.mustEqual(newName.value)
    }

}