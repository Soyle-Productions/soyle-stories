package character.createArcSection

import com.soyle.stories.character.createArcSection.CreateArcSectionController
import com.soyle.stories.character.createArcSection.CreateArcSectionControllerImpl
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcSectionAndCoverInScene
import doubles.ControlledThreadTransformer
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class CreateArcSectionControllerTest {

    private val threadTransformer: ControlledThreadTransformer = ControlledThreadTransformer()
    private val createAndCoverUseCase: CreateCharacterArcSectionAndCoverInScene
    private val createAndCoverOutputPort: CreateCharacterArcSectionAndCoverInScene.OutputPort
    private val controller: CreateArcSectionController

    private val characterId = UUID.randomUUID().toString()
    private val themeId = UUID.randomUUID().toString()
    private val sceneId = UUID.randomUUID().toString()
    private val sectionTemplateId = UUID.randomUUID().toString()
    private val value = UUID.randomUUID().toString()

    private var createAndCoverRequest: CreateCharacterArcSectionAndCoverInScene.RequestModel? = null
    private var createAndCoverRequestedOutput: CreateCharacterArcSectionAndCoverInScene.OutputPort? = null

    @Nested
    inner class createArcSectionAndCoverInScene {

        private fun createArcSectionAndCoverInScene() {
            controller.createArcSectionAndCoverInScene(
                characterId, themeId, sectionTemplateId, value, sceneId
            )
        }

        @Test
        fun `should call create arc section and cover use case`() {
            createArcSectionAndCoverInScene()

            val request = createAndCoverRequest ?: error("create arc section and cover use case not called")
            assertEquals(characterId, request.characterId.toString()) { "Incorrect character id in request" }
            assertEquals(themeId, request.themeId.toString()) { "Incorrect theme id in request" }
            assertEquals(sceneId, request.sceneId.toString()) { "Incorrect scene id in request" }
            assertEquals(
                sectionTemplateId,
                request.sectionTemplateId.toString()
            ) { "Incorrect section template id in request" }
            assertEquals(value, request.value) { "Incorrect value in request" }
            assertEquals(createAndCoverOutputPort, createAndCoverRequestedOutput)
        }

        @Test
        fun `should not block calling thread`() {
            threadTransformer.ensureRunAsync(::createAndCoverRequest::get) {
                createArcSectionAndCoverInScene()
            }
        }

    }

    init {
        createAndCoverUseCase = object : CreateCharacterArcSectionAndCoverInScene {
            override suspend fun invoke(
                request: CreateCharacterArcSectionAndCoverInScene.RequestModel,
                output: CreateCharacterArcSectionAndCoverInScene.OutputPort
            ) {
                createAndCoverRequest = request
                createAndCoverRequestedOutput = output
            }
        }
        createAndCoverOutputPort = object : CreateCharacterArcSectionAndCoverInScene.OutputPort {
            override suspend fun characterArcCreatedAndCoveredInScene(
                response: CreateCharacterArcSectionAndCoverInScene.ResponseModel
            ) {}
        }
        controller = CreateArcSectionControllerImpl(threadTransformer, createAndCoverUseCase, createAndCoverOutputPort)
    }

}