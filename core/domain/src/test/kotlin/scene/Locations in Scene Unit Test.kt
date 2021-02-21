package com.soyle.stories.domain.scene

import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import org.junit.jupiter.api.Test

class `Locations in Scene Unit Test` {

    private val scene = makeScene()
    private val location = makeLocation()

    @Test
    fun `can use a location in a scene`() {
        val update = scene.withLocationLinked(location)
        update as Updated
        update.event.sceneId.mustEqual(scene.id)
        update.event.sceneSetting.id.mustEqual(location.id)
        update.event.sceneSetting.locationName.mustEqual(location.name.value)
        update.scene.settings.containsEntityWithId(location.id).mustEqual(true)
    }

    @Test
    fun `using the same location should emit no update`() {
        val update = scene.withLocationLinked(location).scene.withLocationLinked(location)
        update as NoUpdate
        update.scene.settings.containsEntityWithId(location.id).mustEqual(true)
    }

}