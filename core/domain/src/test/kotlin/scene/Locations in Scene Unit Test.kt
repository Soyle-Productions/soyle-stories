package com.soyle.stories.domain.scene

import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.events.LocationRemovedFromScene
import com.soyle.stories.domain.scene.events.LocationUsedInScene
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.SceneUpdate.UnSuccessful
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Locations in Scene Unit Test` {

    private val scene = makeScene()
    private val location = makeLocation()

    @Test
    fun `can use a location in a scene`() {
        val update = scene.withLocationLinked(location)
        update as Successful
        update.event.sceneId.mustEqual(scene.id)
        update.event.locationId.mustEqual(location.id)
        update.event.locationName.mustEqual(location.name.value)
        update.scene.settings.containsEntityWithId(location.id).mustEqual(true)
    }

    @Test
    fun `using the same location should emit no update`() {
        val update = scene.withLocationLinked(location).scene.withLocationLinked(location)
        update as UnSuccessful
        update.scene.settings.containsEntityWithId(location.id).mustEqual(true)
    }

    @Test
    fun `can rename location`() {
        val newName = SingleNonBlankLine.create(singleLine("New Name"))!!
        val update = scene.withLocationLinked(location).scene.withLocationRenamed(location.withName(newName).location)
        update as Successful
        update.event.sceneId.mustEqual(scene.id)
        update.event.sceneSettingLocation.id.mustEqual(location.id)
        update.event.sceneSettingLocation.locationName.mustEqual(newName.value)
        update.scene.settings.containsEntityWithId(location.id).mustEqual(true)
    }

    @Test
    fun `renaming a location with the same name should emit no update`() {
        val update = scene.withLocationLinked(location).scene.withLocationRenamed(location)
        update as UnSuccessful
        update.scene.id.mustEqual(scene.id)
        update.scene.settings.getEntityById(location.id)!!.locationName.mustEqual(location.name.value)
    }

    @Test
    fun `renaming a location that the scene doesn't have should fail`() {
        val error = assertThrows<SceneDoesNotUseLocation> {
            scene.withLocationRenamed(location)
        }
        error.sceneId.mustEqual(scene.id)
        error.locationId.mustEqual(location.id)
    }

    @Test
    fun `can replace setting with another location`() {
        val replacement = makeLocation()
        val update = scene.withLocationLinked(location)
            .scene.withSetting(location.id)!!.replacedWith(replacement)

        update as Successful
        update.scene.contains(location.id).mustEqual(false)
        update.scene.settings.getEntityById(replacement.id)!!.locationName.mustEqual(replacement.name.value)
        update.event.mustEqual(
            LocationRemovedFromScene(
                scene.id,
                location.id,
                replacedBy = LocationUsedInScene(scene.id, replacement.id, replacement.name.value)
            )
        )
    }

    @Test
    fun `cannot replace setting with same location`() {
        val update = scene.withLocationLinked(location)
            .scene.withSetting(location.id)!!.replacedWith(location)

        update as UnSuccessful
        update.reason.mustEqual(SceneSettingCannotBeReplacedBySameLocation(scene.id, location.id))
    }

}