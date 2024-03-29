package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import com.soyle.stories.usecase.scene.reorderScene.ReorderSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ReorderSceneUnitTest {

    private val sceneId = Scene.Id()
    private val otherSceneIds = List(5) { Scene.Id() }
    private val sceneName = NonBlankString.create("Target Scene")!!
    private val sceneProse = Prose.Id()
    private val projectId = Project.Id()

    private var savedSceneIds: List<Scene.Id>? = null
    private var result: Any? = null


    @Test
    fun `scene does not exist`() {
        `when scene is reordered`(0)
        result shouldBe sceneDoesNotExist(sceneId.uuid)
        Assertions.assertNull(savedSceneIds)
    }

    @Test
    fun `index less than 0`() {
        `given scene exists`()
        `when scene is reordered`(-1)
        result shouldBe { it as IndexOutOfBoundsException }
        Assertions.assertNull(savedSceneIds)
    }

    @Test
    fun `index greater than scene count`() {
        `given scene exists`()
        `when scene is reordered`(2)
        result shouldBe { it as IndexOutOfBoundsException }
        Assertions.assertNull(savedSceneIds)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1])
    fun `scene already at index`(insertIndex: Int) {
        `given scene exists`()
        `when scene is reordered`(insertIndex)
        result shouldBe responseModel()
        Assertions.assertNull(savedSceneIds)
    }

    @Test
    fun `scene at different index`() {
        `given scene exists`(existingSceneCount = 5, atIndex = 1)
        `when scene is reordered`(3)
        result shouldBe responseModel(
            initialIndex = 1, newIndex = 2, updates =
            listOf(otherSceneIds[2] to 1)
        )
        assertEquals(
            listOf(otherSceneIds[0], otherSceneIds[2], sceneId, otherSceneIds[3], otherSceneIds[4]),
            savedSceneIds
        )
    }

    @Test
    fun `move backward`() {
        `given scene exists`(existingSceneCount = 5, atIndex = 3)
        `when scene is reordered`(0)
        result shouldBe responseModel(
            initialIndex = 3, newIndex = 0, updates =
            listOf(otherSceneIds[0] to 1, otherSceneIds[1] to 2, otherSceneIds[2] to 3)
        )
        assertEquals(
            listOf(sceneId, otherSceneIds[0], otherSceneIds[1], otherSceneIds[2], otherSceneIds[4]),
            savedSceneIds
        )
    }

    @Test
    fun `move to end`() {
        `given scene exists`(existingSceneCount = 4, atIndex = 1)
        `when scene is reordered`(4)
        result shouldBe responseModel(
            initialIndex = 1, newIndex = 3, updates =
            listOf(otherSceneIds[2] to 1, otherSceneIds[3] to 2)
        )
        assertEquals(
            listOf(otherSceneIds[0], otherSceneIds[2], otherSceneIds[3], sceneId),
            savedSceneIds
        )
    }

    private val sceneRepository = SceneRepositoryDouble(onUpdateSceneOrder = { _, it -> savedSceneIds = it })
    private var originalSceneOrder: List<Scene.Id> = listOf()

    private fun `given scene exists`(existingSceneCount: Int = 1, atIndex: Int = 0) {
        sceneRepository.sceneOrder[projectId] = List(existingSceneCount) {
            val sceneId = if (it == atIndex) sceneId else otherSceneIds[it]
            val sceneName = if (it == atIndex) sceneName else nonBlankStr()
            val proseId = if (it == atIndex) sceneProse else Prose.Id()
            makeScene(sceneId, projectId, sceneName, proseId = proseId)
        }.onEach {
            sceneRepository.scenes[it.id] = it
        }.map { it.id }
        originalSceneOrder = sceneRepository.sceneOrder[projectId]!!
    }

    private fun `when scene is reordered`(index: Int) {
        val useCase: ReorderScene = ReorderSceneUseCase(sceneRepository)
        val output = object : ReorderScene.OutputPort {
            override fun failedToReorderScene(failure: Exception) {
                result = failure
            }

            override fun sceneReordered(response: ReorderScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(ReorderScene.RequestModel(sceneId.uuid, index, SceneLocaleDouble()), output)
        }
    }

    private fun responseModel(
        initialIndex: Int = 0,
        newIndex: Int = 0,
        updates: List<Pair<Scene.Id, Int>> = listOf()
    ): (Any?) -> Unit = { actual ->
        actual as ReorderScene.ResponseModel
        assertEquals(SceneItem(sceneId.uuid, sceneProse, sceneName.value, newIndex), actual.scene)
        assertEquals(initialIndex, actual.oldIndex) { "Original index not correctly output" }
        assertUpdatedItemsOutput(actual, updates.toMap())
    }

    private fun assertUpdatedItemsOutput(actual: ReorderScene.ResponseModel, updates: Map<Scene.Id, Int>) {
        assertEquals(
            updates.map {
                SceneItem(
                    it.key.uuid,
                    sceneRepository.scenes.getValue(it.key).proseId,
                    sceneRepository.scenes.getValue(it.key).name.value,
                    it.value
                )
            },
            actual.updatedScenes
        ) { "Not all expected scenes in output.\nTargeted Scene: ${actual.scene}" }
    }

}