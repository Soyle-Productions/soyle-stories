package com.soyle.stories.domain.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.str
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.SceneUpdate.UnSuccessful
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Scene Frame Value Unit Test` {

    private val scene: Scene = makeScene()

    @Nested
    inner class `Can Update Scene Conflict` {

        private val newConflict = SceneConflict(str())

        @Test
        fun `should update scene conflict`() {
            val update = scene.withSceneFrameValue(newConflict)
            update as Successful
            update.scene.conflict.mustEqual(newConflict)
        }

        @Test
        fun `should output scene frame value changed event`() {
            val update = scene.withSceneFrameValue(newConflict)
            update as Successful
            with (update.event) {
                sceneId.mustEqual(scene.id)
                newValue.mustEqual(newConflict)
            }
        }

        @Test
        fun `should output no update when conflict is the same`() {
            val update = scene.withSceneFrameValue(newConflict)
                .scene.withSceneFrameValue(newConflict)
            update as UnSuccessful
        }

    }

    @Nested
    inner class `Can Update Scene Resolution` {

        private val newResolution = SceneResolution(str())

        @Test
        fun `should update scene resolution`() {
            val update = scene.withSceneFrameValue(newResolution)
            update as Successful
            update.scene.resolution.mustEqual(newResolution)
        }

        @Test
        fun `should output scene frame value changed event`() {
            val update = scene.withSceneFrameValue(newResolution)
            update as Successful
            with (update.event) {
                sceneId.mustEqual(scene.id)
                newValue.mustEqual(newResolution)
            }
        }

        @Test
        fun `should output no update when conflict is the same`() {
            val update = scene.withSceneFrameValue(newResolution)
                .scene.withSceneFrameValue(newResolution)
            update as UnSuccessful
        }

    }

}