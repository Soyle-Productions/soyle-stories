package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.SceneConflict
import com.soyle.stories.domain.scene.SceneResolution
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.str
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrame
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrameUseCase
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValue
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValueUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Scene Frame Can be Read After Setting Int Test` {

    private val scene = makeScene()

    private var sceneFrame: GetSceneFrame.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble()
    private val getSceneFrame: GetSceneFrame = GetSceneFrameUseCase(sceneRepository)
    private val setSceneFrameValue: SetSceneFrameValue = SetSceneFrameValueUseCase(sceneRepository)

    private val getSceneFrameOutput = object : GetSceneFrame.OutputPort {
        override suspend fun receiveSceneFrame(response: GetSceneFrame.ResponseModel) {
            sceneFrame = response
        }
    }
    private val setSceneFrameValueOutput = object : SetSceneFrameValue.OutputPort {
        override suspend fun sceneFrameValueSet(response: SetSceneFrameValue.ResponseModel) {}
    }

    init {
        sceneRepository.givenScene(scene)
    }

    @Test
    fun `initial frame values should be blank`() = runBlocking {
        getSceneFrame(scene.id, getSceneFrameOutput)
        sceneFrame!!.sceneConflict.mustEqual("")
        sceneFrame!!.sceneResolution.mustEqual("")
    }

    private val setConflict = SceneConflict("Scene Conflict ${str()}")
    private val setResolution = SceneResolution("Scene Resolution ${str()}")

    @Nested
    inner class `Given Conflict has been set`
    {
        init {
            runBlocking {
                setSceneFrameValue(scene.id, setConflict, setSceneFrameValueOutput)
            }
        }

        @Test
        fun `should read set conflict value`() = runBlocking {
            getSceneFrame(scene.id, getSceneFrameOutput)
            sceneFrame!!.sceneConflict.mustEqual(setConflict.value)
            sceneFrame!!.sceneResolution.mustEqual("")
        }
    }

    @Nested
    inner class `Given Resolution has been set`
    {
        init {
            runBlocking {
                setSceneFrameValue(scene.id, setResolution, setSceneFrameValueOutput)
            }
        }

        @Test
        fun `should read set conflict value`() = runBlocking {
            getSceneFrame(scene.id, getSceneFrameOutput)
            sceneFrame!!.sceneConflict.mustEqual("")
            sceneFrame!!.sceneResolution.mustEqual(setResolution.value)
        }
    }

    @Nested
    inner class `Given Entire Frame has set values`
    {
        init {
            runBlocking {
                setSceneFrameValue(scene.id, setConflict, setSceneFrameValueOutput)
                setSceneFrameValue(scene.id, setResolution, setSceneFrameValueOutput)
            }
        }

        @Test
        fun `should read set conflict value`() = runBlocking {
            getSceneFrame(scene.id, getSceneFrameOutput)
            sceneFrame!!.sceneConflict.mustEqual(setConflict.value)
            sceneFrame!!.sceneResolution.mustEqual(setResolution.value)
        }
    }

}