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
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.FX
import java.util.*
import kotlin.math.min
import kotlin.reflect.KFunction

class MoralArgumentViewUnitTest : FxRobot() {

    private val themeId = UUID.randomUUID()

    private val scope = MoralArgumentScope(
        makeProjectScope(),
        "",
        MoralArgument(themeId)
    )
    private val moralArgumentView by lazy { scope.get<MoralArgumentView>() }
    private val state by lazy { scope.get<MoralArgumentState>() }

    private val viewListenerCallLog = mutableMapOf<KFunction<*>, Map<String, Any?>>()

    @Nested
    inner class Initialize {

        @Test
        fun `should call for initial state`() {
            val request =
                viewListenerCallLog[MoralArgumentViewListener::getValidState] ?: error("Did not request valid state")
            assertEquals(emptyMap<String, Any?>(), request)
        }

    }

    @Nested
    inner class `Update State` {

        init {
            state.update {
                MoralArgumentViewModel(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    null,
                    null,
                    "",
                    "",
                    { "" },
                    null
                )
            }
        }

        @Test
        fun `moral problem`() {
            val moralProblemLabel = "Moral Problem t4y"
            val moralProblemValue = "Moral Problem Value 49y"

            state.updateOrInvalidated {
                copy(
                    moralProblemLabel = moralProblemLabel,
                    moralProblemValue = moralProblemValue
                )
            }

            assertThat(moralArgumentView) {
                andMoralProblemField {
                    hasLabel(moralProblemLabel)
                    hasValue(moralProblemValue)
                }
            }
        }

        @Test
        fun `theme line`() {
            val themeLineLabel = "Theme Line 8te"
            val themeLineValue = "Moral Problem Value 0nl"

            state.updateOrInvalidated {
                copy(
                    themeLineLabel = themeLineLabel,
                    themeLineValue = themeLineValue
                )
            }

            assertThat(moralArgumentView) {
                andThemeLineField {
                    hasLabel(themeLineLabel)
                    hasValue(themeLineValue)
                }
            }
        }

        @Test
        fun `Perspective Character`() {
            val perspectiveCharacterLabel = "Perspective Character 58y"
            val noPerspectiveCharacterLabel = "No perspective Character g6t"

            state.updateOrInvalidated {
                copy(
                    perspectiveCharacterLabel = perspectiveCharacterLabel,
                    noPerspectiveCharacterLabel = noPerspectiveCharacterLabel
                )
            }

            assertThat(moralArgumentView) {
                andPerspectiveCharacterField {
                    hasLabel(perspectiveCharacterLabel)
                    hasValueDisplayed(noPerspectiveCharacterLabel)
                }
            }
        }

        @Test
        fun `Perspective Character Selected`() {
            val noPerspectiveCharacterLabel = "No perspective Character g6t"
            val selectedCharacter = CharacterItemViewModel(
                "",
                "Character Name 5y8",
                ""
            )

            state.updateOrInvalidated {
                copy(
                    noPerspectiveCharacterLabel = noPerspectiveCharacterLabel,
                    selectedPerspectiveCharacter = selectedCharacter
                )
            }

            assertThat(moralArgumentView) {
                andPerspectiveCharacterField {
                    hasValueDisplayed(selectedCharacter.characterName)
                }
            }
        }

        @Test
        fun `Loading Perspective Characters`() {
            val loadingLabel = "Loading 09u"

            state.updateOrInvalidated {
                copy(
                    loadingPerspectiveCharactersLabel = loadingLabel,
                    availablePerspectiveCharacters = null
                )
            }

            assertThat(moralArgumentView) {
                andPerspectiveCharacterField {
                    onlyHasItems(listOf(loadingLabel))
                }
            }
        }

        @Test
        fun `Available Perspective Characters When Not Showing List`() {
            val loadingLabel = "Loading 09u"
            val availablePerspectiveCharacters = List(10) {
                AvailablePerspectiveCharacterViewModel(
                    "",
                    "Character $it",
                    isMajorCharacter = it % 2 == 0
                )
            }

            state.updateOrInvalidated {
                copy(
                    loadingPerspectiveCharactersLabel = loadingLabel,
                    availablePerspectiveCharacters = availablePerspectiveCharacters
                )
            }

            assertThat(moralArgumentView) {
                andPerspectiveCharacterField {
                    onlyHasItems(listOf(loadingLabel))
                }
            }
        }

        @Test
        fun `Available Perspective Characters`() {
            val availablePerspectiveCharacters = List(10) {
                AvailablePerspectiveCharacterViewModel(
                    "",
                    "Character $it",
                    isMajorCharacter = it % 2 == 0
                )
            }
            val minorCharacterMessageGenerator: (AvailablePerspectiveCharacterViewModel) -> String = {
                "This is a generated message for ${it.characterName}"
            }

            interact {
                MoralArgumentViewDriver(moralArgumentView).getPerspectiveCharacterSelection().show()
            }

            state.updateOrInvalidated {
                copy(
                    availablePerspectiveCharacters = availablePerspectiveCharacters,
                    unavailableCharacterMessage = minorCharacterMessageGenerator
                )
            }

            assertThat(moralArgumentView) {
                andPerspectiveCharacterField {
                    onlyHasItems(
                        availablePerspectiveCharacters.map { it.characterName }
                    )
                    eachDiscouragedItemHasMessage(minorCharacterMessageGenerator)
                }
            }
        }

        @Test
        fun `Character Arc Sections`() {
            val selectedCharacter = CharacterItemViewModel(
                "",
                "Character Name 5y8",
                ""
            )
            val arcSections = List(8) {
                MoralArgumentSectionViewModel(
                    it.toString(),
                    "Section $it",
                    "Section Value ${"oinaset${it}asru".hashCode()}"
                )
            }

            state.updateOrInvalidated {
                copy(
                    selectedPerspectiveCharacter = selectedCharacter,
                    sections = arcSections
                )
            }

            assertThat(moralArgumentView) {
                onlyHasArcSections(arcSections.map { it.arcSectionName })
                andEachArcSection {
                    hasValue(arcSections[it].arcSectionValue)
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
                object : MoralArgumentViewListener {
                    override fun getValidState() {
                        viewListenerCallLog[MoralArgumentViewListener::getValidState] = mapOf()
                    }

                    override fun outlineMoralArgument(characterId: String) {
                        viewListenerCallLog[MoralArgumentViewListener::outlineMoralArgument] = mapOf(
                            "characterId" to characterId
                        )
                    }

                    override fun getAvailableArcSectionTypesToAdd(characterId: String) {
                        viewListenerCallLog[MoralArgumentViewListener::getAvailableArcSectionTypesToAdd] = mapOf(
                            "characterId" to characterId
                        )
                    }

                    override fun addCharacterArcSectionType(characterId: String, sectionTemplateId: String) {
                        viewListenerCallLog[MoralArgumentViewListener::addCharacterArcSectionType] = mapOf(
                            "characterId" to characterId,
                            "sectionTemplateId" to sectionTemplateId
                        )
                    }

                    override fun addCharacterArcSectionTypeAtIndex(
                        characterId: String,
                        sectionTemplateId: String,
                        index: Int
                    ) {
                        viewListenerCallLog[MoralArgumentViewListener::addCharacterArcSectionTypeAtIndex] = mapOf(
                            "characterId" to characterId,
                            "sectionTemplateId" to sectionTemplateId,
                            "index" to index,
                        )
                    }

                    override fun setMoralProblem(problem: String) {
                        viewListenerCallLog[MoralArgumentViewListener::setMoralProblem] = mapOf(
                            "problem" to problem
                        )
                    }

                    override fun setThemeLine(themeLine: String) {
                        viewListenerCallLog[MoralArgumentViewListener::setThemeLine] = mapOf(
                            "themeLine" to themeLine
                        )
                    }

                    override fun setValueOfArcSection(characterId: String, arcSectionId: String, value: String) {
                        viewListenerCallLog[MoralArgumentViewListener::setValueOfArcSection] = mapOf(
                            "characterId" to characterId,
                            "arcSectionId" to arcSectionId,
                            "value" to value
                        )
                    }

                }
            }
        }
        FX.setPrimaryStage(FX.defaultScope, FxToolkit.registerPrimaryStage())
        moralArgumentView
    }

}