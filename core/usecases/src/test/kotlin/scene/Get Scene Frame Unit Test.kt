package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.SceneConflict
import com.soyle.stories.domain.scene.SceneResolution
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.str
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrame
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrameUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Get Scene Frame Unit Test` {

    private val scene = makeScene(
        conflict = SceneConflict("Scene Conflict ${str()}"),
        resolution = SceneResolution("Scene Resolution ${str()}")
    )

    private var result: GetSceneFrame.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble()

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            getSceneFrame()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists`
    {
        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should output scene id`() {
            getSceneFrame()
            result!!.sceneId.mustEqual(scene.id)
        }

        @Test
        fun `should output scene conflict value`() {
            getSceneFrame()
            result!!.sceneConflict.mustEqual(scene.conflict.value)
        }

        @Test
        fun `should output scene resolution value`() {
            getSceneFrame()
            result!!.sceneResolution.mustEqual(scene.resolution.value)
        }
    }

    private fun getSceneFrame() {
        val useCase: GetSceneFrame = GetSceneFrameUseCase(sceneRepository)
        val output = object : GetSceneFrame.OutputPort {
            override suspend fun receiveSceneFrame(response: GetSceneFrame.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, output)
        }
    }

}