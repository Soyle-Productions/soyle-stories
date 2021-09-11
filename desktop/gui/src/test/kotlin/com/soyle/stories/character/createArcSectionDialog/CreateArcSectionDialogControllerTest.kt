package com.soyle.stories.character.createArcSectionDialog

import com.soyle.stories.character.createArcSection.CreateArcSectionController
import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogController
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogPresenter
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogViewModel
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.character.CharacterArcTemplate
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.doubles.ControlledThreadTransformer
import com.soyle.stories.gui.View
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.GetAvailableCharacterArcSectionTypesForCharacterArc
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

        private val themeId = Theme.Id()
        private val characterId = Character.Id()

        @Test
        fun `should call get availability use case`() {
            controller.getValidState(themeId, characterId)

            val request = getAvailabilityRequest ?: error("use case was not called")
            assertEquals(themeId.uuid, request[0]) { "themeId UUID does not match expected" }
            assertEquals(characterId.uuid, request[1]) { "characterId UUID does not match expected" }
            (request[2] as CreateArcSectionDialogPresenter)
        }

        @Test
        fun `should not block calling thread`() {
            threadTransformer.ensureRunAsync(::getAvailabilityRequest::get) {
                controller.getValidState(themeId, characterId)
            }
        }

    }

    @Nested
    inner class createArcSection {

        private val themeId = Theme.Id()
        private val characterId = Character.Id()
        private val sectionTemplateId = CharacterArcTemplateSection.Id()
        private val value: String = "value 252"
        private val sceneId: String = "scene id"

        @Test
        fun `should call create arc section controller`() {
            controller.createArcSection(
                characterId,
                themeId,
                sectionTemplateId,
                value,
            )

            val request = createArcSectionParameters ?: error("create arc section controller not called")
            listOf(
                characterId,
                themeId,
                sectionTemplateId,
                value
            ).forEachIndexed { index, s ->
                assertEquals(s, request[index])
            }
        }

    }

    @Nested
    inner class modifyArcSection {

        private val themeId = Theme.Id()
        private val characterId = Character.Id()
        private val sectionId = CharacterArcSection.Id(UUID.randomUUID())
        private val value: String = "value 252"
        private val sceneId: String = "scene id"

        @Test
        fun `should call cover arc section in scene controller`() {
            controller.modifyArcSection(
                characterId,
                themeId,
                sectionId,
                value,
            )

            val request = modifyArcSectionParameters ?: error("modify arc section controller not called")
            listOf(
                themeId.uuid.toString(),
                characterId.uuid.toString(),
                sectionId.uuid.toString(),
                value
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
                TODO("Not yet implemented")
            }

            override fun createArcSection(
                characterId: Character.Id,
                themeId: Theme.Id,
                sectionTemplateId: CharacterArcTemplateSection.Id,
                value: String
            ): Job {
                createArcSectionParameters = listOf(
                    characterId, themeId, sectionTemplateId, value
                )
                return Job()
            }
        }
        modifyArcSectionController = object : ChangeSectionValueController {
            override fun changeValueOfArcSection(
                themeId: String,
                characterId: String,
                arcSectionId: String,
                value: String
            ): Job {
                modifyArcSectionParameters = listOf(themeId, characterId, arcSectionId, value)
                return Job()
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
            createArcSectionController,
            modifyArcSectionController,
            object : View.Nullable<CreateArcSectionDialogViewModel> {
                override fun updateIf(
                    condition: CreateArcSectionDialogViewModel.() -> Boolean,
                    update: CreateArcSectionDialogViewModel.() -> CreateArcSectionDialogViewModel
                ) {
                    TODO("Not yet implemented")
                }

                override fun updateOrInvalidated(update: CreateArcSectionDialogViewModel.() -> CreateArcSectionDialogViewModel) {
                    TODO("Not yet implemented")
                }

                override val viewModel: CreateArcSectionDialogViewModel
                    get() = TODO("Not yet implemented")

                override fun update(update: CreateArcSectionDialogViewModel?.() -> CreateArcSectionDialogViewModel) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

}