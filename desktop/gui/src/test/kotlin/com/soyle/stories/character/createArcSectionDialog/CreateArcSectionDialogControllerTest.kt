package com.soyle.stories.character.createArcSectionDialog

import com.soyle.stories.character.createArcSection.CreateArcSectionController
import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.doubles.ControlledThreadTransformer
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.GetAvailableCharacterArcSectionTypesForCharacterArc
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class CreateArcSectionDialogControllerTest {


    private val threadTransformer = ControlledThreadTransformer()
    private val getAvailability: GetAvailableCharacterArcSectionTypesForCharacterArc
    private val getAvailabilityOutputPort: GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort
    private val createArcSectionController: CreateArcSectionController
    private val modifyArcSectionController: ChangeSectionValueController
    private val controller: CreateArcSectionDialogController

    private var getAvailabilityRequest: List<Any?>? = null
    private var createArcSectionParameters: List<Any?>? = null
    private var modifyArcSectionParameters: List<Any?>? = null

    @Nested
    inner class getValidState {

        private val themeId = UUID.randomUUID()
        private val characterId = UUID.randomUUID()

        @Test
        fun `should call get availability use case`() {
            controller.getValidState(themeId.toString(), characterId.toString())

            val request = getAvailabilityRequest ?: error("use case was not called")
            assertEquals(themeId, request[0]) { "themeId UUID does not match expected" }
            assertEquals(characterId, request[1]) { "characterId UUID does not match expected" }
            assertEquals(getAvailabilityOutputPort, request[2]) { "Output Port does not match expected" }
        }

        @Test
        fun `should not block calling thread`() {
            threadTransformer.ensureRunAsync(::getAvailabilityRequest::get) {
                controller.getValidState(themeId.toString(), characterId.toString())
            }
        }

    }

    @Nested
    inner class createArcSection {

        private val characterId: String = "character id"
        private val themeId: String = "theme id"
        private val sectionTemplateId: String = "section template id"
        private val value: String = "value 252"
        private val sceneId: String = "scene id"

        @Test
        fun `should call create arc section controller`() {
            controller.createArcSection(
                characterId,
                themeId,
                sectionTemplateId,
                sceneId,
                value,
            )

            val request = createArcSectionParameters ?: error("create arc section controller not called")
            listOf(
                characterId,
                themeId,
                sectionTemplateId,
                value,
                sceneId
            ).forEachIndexed { index, s ->
                assertEquals(s, request[index])
            }
        }

    }

    @Nested
    inner class modifyArcSection {

        private val characterId: String = "character id"
        private val themeId: String = "theme id"
        private val sectionId: String = "section id"
        private val value: String = "value 252"
        private val sceneId: String = "scene id"

        @Test
        fun `should call cover arc section in scene controller`() {
            controller.modifyArcSection(
                characterId,
                themeId,
                sectionId,
                sceneId,
                value,
            )

            val request = modifyArcSectionParameters ?: error("modify arc section controller not called")
            listOf(
                themeId,
                characterId,
                sectionId,
                value,
                sceneId
            ).forEachIndexed { index, s ->
                assertEquals(s, request[index])
            }
        }

    }

    init {
        getAvailability = object : GetAvailableCharacterArcSectionTypesForCharacterArc {
            override suspend fun invoke(
                themeId: UUID,
                characterId: UUID,
                output: GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort
            ) {
                getAvailabilityRequest = listOf(themeId, characterId, output)
            }
        }
        getAvailabilityOutputPort = object : GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort {
            override suspend fun receiveAvailableCharacterArcSectionTypesForCharacterArc(response: AvailableCharacterArcSectionTypesForCharacterArc) {}
        }
        createArcSectionController = object : CreateArcSectionController {
            override fun createArcSectionAndCoverInScene(
                characterId: String,
                themeId: String,
                sectionTemplateId: String,
                value: String,
                sceneId: String
            ) {
                createArcSectionParameters = listOf(
                    characterId, themeId, sectionTemplateId, value, sceneId
                )
            }
        }
        modifyArcSectionController = object : ChangeSectionValueController {
            override fun changeValueOfArcSection(
                themeId: String,
                characterId: String,
                arcSectionId: String,
                value: String
            ) {
                modifyArcSectionParameters = listOf(themeId, characterId, arcSectionId, value)
            }

            override fun changeValueOfArcSectionAndCoverInScene(
                themeId: String,
                characterId: String,
                arcSectionId: String,
                value: String,
                sceneId: String
            ) {
                modifyArcSectionParameters = listOf(themeId, characterId, arcSectionId, value, sceneId)
            }

            override fun changeDesire(themeId: String, characterId: String, desire: String) {
                modifyArcSectionParameters = listOf(themeId, characterId, desire)
            }

            override fun setMoralWeakness(themeId: String, characterId: String, weakness: String) {
                modifyArcSectionParameters = listOf(themeId, characterId, weakness)
            }

            override fun setPsychologicalWeakness(themeId: String, characterId: String, weakness: String) {
                modifyArcSectionParameters = listOf(themeId, characterId, weakness)
            }
        }
        controller = CreateArcSectionDialogController(
            threadTransformer,
            getAvailability,
            getAvailabilityOutputPort,
            createArcSectionController,
            modifyArcSectionController
        )
    }

}