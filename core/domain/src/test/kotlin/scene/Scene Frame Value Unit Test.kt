package com.soyle.stories.domain.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.str
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
            update as Updated
            update.scene.conflict.mustEqual(newConflict)
        }

        @Test
        fun `should output scene frame value changed event`() {
            val update = scene.withSceneFrameValue(newConflict)
            update as Updated
            with (update.event) {
                sceneId.mustEqual(scene.id)
                newValue.mustEqual(newConflict)
            }
        }

        @Test
        fun `should output no update when conflict is the same`() {
            val update = scene.withSceneFrameValue(newConflict)
                .scene.withSceneFrameValue(newConflict)
            update as NoUpdate
        }

    }

    @Nested
    inner class `Can Update Scene Resolution` {

        private val newResolution = SceneResolution(str())

        @Test
        fun `should update scene resolution`() {
            val update = scene.withSceneFrameValue(newResolution)
            update as Updated
            update.scene.resolution.mustEqual(newResolution)
        }

        @Test
        fun `should output scene frame value changed event`() {
            val update = scene.withSceneFrameValue(newResolution)
            update as Updated
            with (update.event) {
                sceneId.mustEqual(scene.id)
                newValue.mustEqual(newResolution)
            }
        }

        @Test
        fun `should output no update when conflict is the same`() {
            val update = scene.withSceneFrameValue(newResolution)
                .scene.withSceneFrameValue(newResolution)
            update as NoUpdate
        }

    }

}