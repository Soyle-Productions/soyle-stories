package com.soyle.stories.theme.moralArgument

import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentController
import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.doubles.ControlledThreadTransformer
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.GetAvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.theme.changeThemeDetails.ChangeCentralMoralQuestionController
import com.soyle.stories.theme.changeThemeDetails.ChangeThemeLineController
import com.soyle.stories.theme.outlineMoralArgument.OutlineMoralArgumentController
import com.soyle.stories.theme.usecases.outlineMoralArgument.GetMoralProblemAndThemeLineInTheme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class MoralArgumentControllerUnitTest {

    private val themeId = UUID.randomUUID()
    private val characterId = UUID.randomUUID()
    private val templateSectionId = UUID.randomUUID()

    private val threadTransformer = ControlledThreadTransformer()
    private val forwardedCalls =
        object : GetMoralProblemAndThemeLineInTheme, GetAvailableCharacterArcSectionTypesForCharacterArc,
            OutlineMoralArgumentController, AddArcSectionToMoralArgumentController,
            ChangeCentralMoralQuestionController, ChangeThemeLineController,
            ChangeSectionValueController {
            override fun outlineMoralArgument(themeId: String, characterId: String) {
                forwardedRequest = mapOf(
                    "call" to "outlineMoralArgument",
                    "themeId" to themeId,
                    "characterId" to characterId
                )
            }

            override suspend fun invoke(
                themeId: UUID,
                characterId: UUID,
                output: GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort
            ) {
                forwardedRequest = mapOf(
                    "call" to "getAvailableCharacterArcSectionTypesForCharacterArc",
                    "themeId" to themeId,
                    "characterId" to characterId,
                    "output" to output
                )
            }

            override suspend fun invoke(themeId: UUID, output: GetMoralProblemAndThemeLineInTheme.OutputPort) {
                forwardedRequest = mapOf(
                    "call" to "getMoralProblemAndThemeLineInTheme",
                    "themeId" to themeId,
                    "output" to output
                )
            }

            override fun addCharacterArcSectionToMoralArgument(
                themeId: String,
                characterId: String,
                templateSectionId: String,
                indexInMoralArgument: Int?
            ) {
                forwardedRequest = mapOf(
                    "call" to "addCharacterArcSectionToMoralArgument",
                    "themeId" to themeId,
                    "characterId" to characterId,
                    "templateSectionId" to templateSectionId,
                    "indexInMoralArgument" to indexInMoralArgument
                )
            }

            override fun updateCentralMoralQuestion(themeId: String, question: String) {
                TODO("Not yet implemented")
            }

            override fun changeThemeLine(themeId: String, themeLine: String) {
                TODO("Not yet implemented")
            }

            override fun changeDesire(themeId: String, characterId: String, desire: String) {
                TODO("Not yet implemented")
            }

            override fun setPsychologicalWeakness(themeId: String, characterId: String, weakness: String) {
                TODO("Not yet implemented")
            }

            override fun setMoralWeakness(themeId: String, characterId: String, weakness: String) {
                TODO("Not yet implemented")
            }

            override fun changeValueOfArcSection(
                themeId: String,
                characterId: String,
                arcSectionId: String,
                value: String
            ) {
                TODO("Not yet implemented")
            }

            override fun changeValueOfArcSectionAndCoverInScene(
                themeId: String,
                characterId: String,
                arcSectionId: String,
                value: String,
                sceneId: String
            ) {
                TODO("Not yet implemented")
            }

        }
    private val output = object : GetMoralProblemAndThemeLineInTheme.OutputPort,
        GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort {
        override suspend fun receiveMoralProblemAndThemeLineInTheme(response: GetMoralProblemAndThemeLineInTheme.ResponseModel) {}
        override suspend fun receiveAvailableCharacterArcSectionTypesForCharacterArc(response: AvailableCharacterArcSectionTypesForCharacterArc) {}
    }
    private val controller: MoralArgumentViewListener = MoralArgumentController(
        themeId.toString(),
        threadTransformer,
        forwardedCalls,
        output,
        forwardedCalls,
        forwardedCalls,
        output,
        forwardedCalls,
        forwardedCalls,
        forwardedCalls,
        forwardedCalls
    )

    private var forwardedRequest: Map<String, Any?>? = null

    @Nested
    inner class `Get Valid State` {

        @Test
        fun `should get moral problem and theme line`() {
            threadTransformer.ensureRunAsync(::forwardedRequest) {
                controller.getValidState()
            }

            assertEquals(
                forwardedRequest, mapOf(
                    "call" to "getMoralProblemAndThemeLineInTheme",
                    "themeId" to themeId,
                    "output" to output
                )
            )
        }

    }

    @Nested
    inner class `Outline Moral Argument` {

        @Test
        fun `should call controller`() {
            controller.outlineMoralArgument(characterId.toString())

            assertEquals(
                forwardedRequest, mapOf(
                    "call" to "outlineMoralArgument",
                    "themeId" to themeId.toString(),
                    "characterId" to characterId.toString()
                )
            )
        }

    }

    @Nested
    inner class `Get Available Arc Section Types to Add` {

        @Test
        fun `should call use case`() {
            threadTransformer.ensureRunAsync(::forwardedRequest) {
                controller.getAvailableArcSectionTypesToAdd(characterId.toString())
            }

            assertEquals(
                forwardedRequest, mapOf(
                    "call" to "getAvailableCharacterArcSectionTypesForCharacterArc",
                    "themeId" to themeId,
                    "characterId" to characterId,
                    "output" to output
                )
            )
        }

    }

    @Nested
    inner class `Add Character Arc Section Type` {

        @Test
        fun `should forward to controller`() {
            controller.addCharacterArcSectionType(characterId.toString(), templateSectionId.toString())

            assertEquals(
                forwardedRequest, mapOf(
                    "call" to "addCharacterArcSectionToMoralArgument",
                    "themeId" to themeId.toString(),
                    "characterId" to characterId.toString(),
                    "templateSectionId" to templateSectionId.toString(),
                    "indexInMoralArgument" to null
                )
            )
        }

        @Test
        fun `if index supplied, call should include supplied index`() {
            controller.addCharacterArcSectionTypeAtIndex(characterId.toString(), templateSectionId.toString(), 8)

            assertEquals(
                forwardedRequest, mapOf(
                    "call" to "addCharacterArcSectionToMoralArgument",
                    "themeId" to themeId.toString(),
                    "characterId" to characterId.toString(),
                    "templateSectionId" to templateSectionId.toString(),
                    "indexInMoralArgument" to 8
                )
            )
        }

    }

}