package com.soyle.stories.theme.moralArgument

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.common.SyncThreadTransformer
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert.Companion.assertThat
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewDriver
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.layout.config.dynamic.MoralArgument
import com.soyle.stories.project.makeProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.FX
import tornadofx.swap
import java.util.*

class `Moral Argument Section Drag and Drop Test` : FxRobot() {

    private val themeId = UUID.randomUUID()

    private val scope = MoralArgumentScope(
        makeProjectScope(),
        "",
        MoralArgument(themeId)
    )
    private val moralArgumentView by lazy { scope.get<MoralArgumentView>() }
    private val state by lazy { scope.get<MoralArgumentState>() }

    private val viewListener = MoralArgumentViewListenerMock()
    private val perspectiveCharacterId = "Character Id y89"

    @BeforeEach
    fun setInitialState() {
        state.update {
            MoralArgumentViewModel(
                moralProblemLabel = "moralProblemLabel",
                moralProblemValue = "moralProblemValue",
                themeLineLabel = "themeLineLabel",
                themeLineValue = "themeLineValue",
                thematicRevelationLabel = "thematicRevelationLabel",
                thematicRevelationValue = "thematicRevelationValue",
                perspectiveCharacterLabel = "perspectiveCharacterLabel",
                noPerspectiveCharacterLabel = "noPerspectiveCharacterLabel",
                selectedPerspectiveCharacter = CharacterItemViewModel(perspectiveCharacterId, "", ""),
                availablePerspectiveCharacters = null,
                loadingPerspectiveCharactersLabel = "loadingPerspectiveCharactersLabel",
                loadingSectionTypesLabel = "loadingSectionTypesLabel",
                createCharacterLabel = "createCharacterLabel",
                unavailableCharacterMessage = { "unavailableCharacterMessage" },
                unavailableSectionTypeMessage = { "unavailableSectionTypeMessage" },
                removeSectionButtonLabel = "Remove",
                sections = List(10) { MoralArgumentSectionViewModel("$it", "Section $it", "value: $it", true) },
                availableSectionTypes = null
            )
        }

        interact {
            moralArgumentView.currentStage?.sizeToScene()
            moralArgumentView.currentStage?.centerOnScreen()
        }
    }

    fun `temp`() = runBlocking {
        while (moralArgumentView.currentStage?.isShowing == true) {
            delay(100)
        }
    }

    @Nested
    inner class `Drag Section to same spot` {

        @Test
        fun `mouse drag should not call view listener`() {
            val handle = MoralArgumentViewDriver(moralArgumentView).getArcSectionDragHandle(4)
            interact {
                drag(handle, MouseButton.PRIMARY)
                    .drop()
            }
            assertEquals(1, viewListener.callLog.size) { "Should not have called view listener.  Instead called: ${viewListener.callLog}" }
        }

        @Test
        fun `keyboard control should not call view listener`() {
            val handle = MoralArgumentViewDriver(moralArgumentView).getArcSectionDragHandle(4)
            interact {
                handle.requestFocus()
                press(KeyCode.ENTER).release(KeyCode.ENTER)
                press(KeyCode.ENTER).release(KeyCode.ENTER)
            }
            assertEquals(1, viewListener.callLog.size) { "Should not have called view listener.  Instead called: ${viewListener.callLog}" }
        }

    }

    @Nested
    inner class `Move up` {

        @Test
        fun `mouse drag to top should call view listener with dropped index`() = runBlocking {
            val driver = MoralArgumentViewDriver(moralArgumentView)
            val handle = driver.getArcSectionDragHandle(4)

            interact {
                drag(handle, MouseButton.PRIMARY)
                    .dropTo(driver.getArcSectionLabel(2))
            }

            assertEquals(
                mapOf("arcSectionId" to "4", "characterId" to perspectiveCharacterId, "index" to 2),
                viewListener.callLog[MoralArgumentViewListener::moveSectionTo]
            )
        }

        @Test
        fun `mouse drag to bottom should call view listener with one more than dropped index`() = runBlocking {
            val driver = MoralArgumentViewDriver(moralArgumentView)
            val handle = driver.getArcSectionDragHandle(4)

            interact {

                drag(handle, MouseButton.PRIMARY)
                    .dropTo(point(driver.getArcSectionValueInput(2)).atPosition(0.0, 1.0))
            }

            assertEquals(
                mapOf("arcSectionId" to "4", "characterId" to perspectiveCharacterId, "index" to 3),
                viewListener.callLog[MoralArgumentViewListener::moveSectionTo]
            )
        }

        @Test
        fun `keyboard control should call view listener with final index`() {
            val driver = MoralArgumentViewDriver(moralArgumentView)
            val handle = driver.getArcSectionDragHandle(4)

            interact {
                handle.requestFocus()
                press(KeyCode.ENTER).release(KeyCode.ENTER)
                    .press(KeyCode.UP).release(KeyCode.UP)
                    .press(KeyCode.UP).release(KeyCode.UP)
                    .press(KeyCode.ENTER).release(KeyCode.ENTER)
            }

            assertEquals(
                mapOf("arcSectionId" to "4", "characterId" to perspectiveCharacterId, "index" to 2),
                viewListener.callLog[MoralArgumentViewListener::moveSectionTo]
            )
        }

        @Test
        fun `reordered section should be reflected in view`() {
            val idAt4 = state.item!!.sections!![4].arcSectionId

            state.updateOrInvalidated {
                copy(
                    sections = sections!!.toMutableList().apply {
                        add(2, removeAt(4))
                    }
                )
            }

            assertThat(moralArgumentView) {
                andSectionAt(2) {
                    hasId(idAt4)
                }
            }

        }
    }

    init {
        scoped<ApplicationScope> {
            provide<ThreadTransformer> { SyncThreadTransformer() }
        }
        scoped<MoralArgumentScope> {
            provide<MoralArgumentViewListener> {
                viewListener
            }
        }
        FX.setPrimaryStage(FX.defaultScope, FxToolkit.registerPrimaryStage())
        interact {
            moralArgumentView.openWindow()
        }
    }

}