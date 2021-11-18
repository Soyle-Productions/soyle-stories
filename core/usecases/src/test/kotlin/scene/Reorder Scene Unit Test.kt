package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.domain.scene.order.SuccessfulSceneOrderUpdate
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import com.soyle.stories.usecase.scene.reorderScene.ReorderSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class `Reorder Scene Unit Test` {

    // Pre Conditions
    /** A project has been started */
    val projectId = Project.Id()
    /** The scene exists */
    val scene = makeScene(projectId = projectId)
    /** At least one other scene exists */
    val otherScene = makeScene(projectId = projectId)

    // Post Conditions
    /** The scene order should be updated */
    var updatedSceneOrder: SceneOrder? = null
    var sceneOrderUpdated: SuccessfulSceneOrderUpdate<Nothing?>? = null

    // repositories
    private val sceneRepository = SceneRepositoryDouble(onUpdateSceneOrder = ::updatedSceneOrder::set)

    // useCase
    private val useCase: ReorderScene = ReorderSceneUseCase(sceneRepository)

    private fun reorderScene(index: Int) = runBlocking {
        useCase.invoke(scene.id, index) {
            sceneOrderUpdated = it.sceneOrderUpdate
        }
    }

    @Test
    fun `when scene does not exist, should throw error`() {
        val error = assertThrows<SceneDoesNotExist> { reorderScene(0) }

        error.sceneId.mustEqual(scene.id.uuid)
        assertNull(updatedSceneOrder)
    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, 1])
        fun `when scene order fails to update, should throw corresponding error`(index: Int) {
            assertThrows<Throwable> { reorderScene(index) }

            assertNull(updatedSceneOrder)
        }

        @Nested
        inner class `Given Other Scenes Exist` {

            init {
                sceneRepository.givenScene(otherScene)
                sceneRepository.sceneOrders[scene.projectId] = SceneOrder.reInstantiate(scene.projectId, listOf(otherScene.id, scene.id))
            }

            @Test
            fun `when current index is provided, should throw error`() {
                assertThrows<Throwable> { reorderScene(1) }

                assertNull(updatedSceneOrder)
            }

            @Test
            fun `when different index is provided, should update scene order and produce event`() {
                reorderScene(0)

                updatedSceneOrder!!
                sceneOrderUpdated!!
            }

        }

    }
}