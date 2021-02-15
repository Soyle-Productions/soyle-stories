package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.*
import com.soyle.stories.domain.str
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValue
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValueUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class `Set Scene Frame Value Unit Test` {

    private val scene = makeScene()

    private var updatedScene: Scene? = null
    private var result: SetSceneFrameValue.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            setSceneFrameValue(SceneConflict(""))
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        private val dynamicTestValues = listOf(
            Scene::conflict to SceneConflict(str()),
            Scene::resolution to SceneResolution(str())
        )

        @TestFactory
        fun `should update scene`() = dynamicTestValues.map { (property, value) ->
            dynamicTest("should update scene's ${property.name}") {
                setSceneFrameValue(value)
                property.get(updatedScene!!).mustEqual(value)
            }
        }

        @TestFactory
        fun `should output event`() = dynamicTestValues.map { (property, value) ->
            dynamicTest("should output event for ${property.name}") {
                setSceneFrameValue(value)
                result!!.sceneFrameValueChanged.mustEqual(SceneFrameValueChanged(scene.id, value))
            }
        }

        @Nested
        inner class `Given Value is the Same` {

            @TestFactory
            fun `should not update scene`() = dynamicTestValues.map { (property, value) ->
                dynamicTest("should not update scene's ${property.name}") {
                    sceneRepository.givenScene(scene.withSceneFrameValue(value).scene)

                    setSceneFrameValue(value)
                    assertNull(updatedScene)

                    sceneRepository.givenScene(scene) // reset to base scene for next test
                }
            }

            @TestFactory
            fun `should output response without event`() = dynamicTestValues.map { (property, value) ->
                dynamicTest("should not output event for ${property.name}") {
                    sceneRepository.givenScene(scene.withSceneFrameValue(value).scene)

                    setSceneFrameValue(value)
                    assertNull(result!!.sceneFrameValueChanged)

                    sceneRepository.givenScene(scene) // reset to base scene for next test
                }
            }

        }

    }

    private fun setSceneFrameValue(value: SceneFrameValue) {
        val useCase: SetSceneFrameValue = SetSceneFrameValueUseCase(sceneRepository)
        val output = object : SetSceneFrameValue.OutputPort {
            override suspend fun sceneFrameValueSet(response: SetSceneFrameValue.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, value, output)
        }
    }

}