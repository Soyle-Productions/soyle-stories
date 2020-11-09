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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
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

    private val viewListener = MoralArgumentViewListenerMock()

    private val viewListenerCallLog: Map<KFunction<*>, Map<String, Any?>>
        get() = viewListener.callLog

    @BeforeEach
    fun setInitialState() {
        state.update {
            MoralArgumentViewModel(
                moralProblemLabel = "",
                moralProblemValue = "",
                themeLineLabel = "",
                themeLineValue = "",
                thematicRevelationLabel = "",
                thematicRevelationValue = "",
                perspectiveCharacterLabel = "",
                noPerspectiveCharacterLabel = "",
                selectedPerspectiveCharacter = null,
                availablePerspectiveCharacters = null,
                loadingPerspectiveCharactersLabel = "",
                loadingSectionTypesLabel = "",
                createCharacterLabel = "",
                unavailableCharacterMessage = { "" },
                unavailableSectionTypeMessage = { "" },
                sections = null,
                availableSectionTypes = null
            )
        }
    }

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

        @Test
        fun `Loading Section Types`() {
            val loadingLabel = "Loading 09u"

            state.updateOrInvalidated {
                copy(
                    loadingSectionTypesLabel = loadingLabel,
                    availableSectionTypes = null
                )
            }

            assertThat(moralArgumentView) {
                andSectionTypeMenu {
                    onlyHasItems(listOf(loadingLabel))
                }
            }
        }

        @Test
        fun `Available Section Types`() = runBlocking {
            val availableSectionTypes = List(10) {
                MoralArgumentSectionTypeViewModel(
                    "",
                    "Type $it",
                    canBeCreated = it % 2 == 0
                )
            }
            val movableSectionTypeMessageGenerator: (MoralArgumentSectionTypeViewModel) -> String = {
                "This is a generated message for ${it.sectionTypeName}"
            }

            state.updateOrInvalidated { copy(sections = List(5) { MoralArgumentSectionViewModel("$it", "", "") }) }

            interact {
                MoralArgumentViewDriver(moralArgumentView).getSectionTypeSelections().first().show()
            }

            state.updateOrInvalidated {
                copy(
                    availableSectionTypes = availableSectionTypes,
                    unavailableSectionTypeMessage = movableSectionTypeMessageGenerator
                )
            }

            assertThat(moralArgumentView) {
                andSectionTypeMenu {
                    onlyHasItems(
                        availableSectionTypes.map { it.sectionTypeName }
                    )
                    eachDiscouragedItemHasMessage(movableSectionTypeMessageGenerator)
                }
            }
        }

        @Test
        fun `thematic revelation`() {
            val thematicRevelationLabel = "Thematic Revelation 756"
            val thematicRevelationValue = "Thematic Revelation Value nbQ"

            state.updateOrInvalidated {
                copy(
                    thematicRevelationLabel = thematicRevelationLabel,
                    thematicRevelationValue = thematicRevelationValue
                )
            }

            assertThat(moralArgumentView) {
                andThematicRevelationField {
                    hasLabel(thematicRevelationLabel)
                    hasValue(thematicRevelationValue)
                }
            }
        }

    }

    @Nested
    inner class `Update Moral Problem` {

        @Nested
        inner class `When Text is Different` {

            private val newMoralProblem = "New Moral Problem tg8i"

            @Test
            fun `should update moral problem`() {
                val driver = MoralArgumentViewDriver(moralArgumentView)
                interact {
                    driver.getMoralProblemFieldInput().apply {
                        requestFocus()
                        text = newMoralProblem
                    }
                    driver.getThemeLineFieldInput().requestFocus()
                }

                assertEquals(
                    mapOf("problem" to newMoralProblem),
                    viewListenerCallLog[MoralArgumentViewListener::setMoralProblem]
                )
            }

        }

        @Nested
        inner class `When Text is the same` {

            @Test
            fun `should not update moral problem`() {
                val driver = MoralArgumentViewDriver(moralArgumentView)
                interact {
                    driver.getMoralProblemFieldInput().apply {
                        requestFocus()
                    }
                    driver.getThemeLineFieldInput().requestFocus()
                }

                assertNull(viewListenerCallLog[MoralArgumentViewListener::setMoralProblem])

            }

        }

    }

    @Nested
    inner class `Update Theme Line` {

        @Nested
        inner class `When Text is Different` {

            private val newThemeLine = "New Theme Line t53a"

            @Test
            fun `should update theme line`() {
                val driver = MoralArgumentViewDriver(moralArgumentView)
                interact {
                    driver.getThemeLineFieldInput().apply {
                        requestFocus()
                        text = newThemeLine
                    }
                    driver.getMoralProblemFieldInput().requestFocus()
                }

                assertEquals(
                    mapOf("themeLine" to newThemeLine),
                    viewListenerCallLog[MoralArgumentViewListener::setThemeLine]
                )
            }

        }

        @Nested
        inner class `When Text is the same` {

            @Test
            fun `should not update theme line`() {
                val driver = MoralArgumentViewDriver(moralArgumentView)
                interact {
                    driver.getThemeLineFieldInput().apply {
                        requestFocus()
                    }
                    driver.getMoralProblemFieldInput().requestFocus()
                }

                assertNull(viewListenerCallLog[MoralArgumentViewListener::setThemeLine])

            }

        }

    }

    @Nested
    inner class `Change Thematic Revelation` {

        private val newRevelation = "New Thematic Revelation c1b"

        @Test
        fun `should update theme line`() {
            val driver = MoralArgumentViewDriver(moralArgumentView)
            interact {
                driver.getThematicRevelationFieldInput().apply {
                    requestFocus()
                    text = newRevelation
                }
                driver.getMoralProblemFieldInput().requestFocus()
            }

            assertEquals(
                mapOf("revelation" to newRevelation),
                viewListenerCallLog[MoralArgumentViewListener::setThematicRevelation]
            )
        }

    }

    @Nested
    inner class `When Perspective Character Selection is Opened` {

        @Test
        fun `should load available perspective characters`() {
            interact {
                MoralArgumentViewDriver(moralArgumentView).getPerspectiveCharacterSelection().show()
            }

            assertEquals(
                emptyMap<String, Any?>(),
                viewListenerCallLog[MoralArgumentViewListener::getPerspectiveCharacters]
            )
        }

    }

    @Nested
    inner class `Select Perspective Character` {

        private val characterId = UUID.randomUUID()
        private val characterName = "Some Character t849tu"

        @Nested
        inner class `When Perspective Character is Major Character` {

            @Test
            fun `should load moral argument sections`() {
                val driver = MoralArgumentViewDriver(moralArgumentView)
                interact {
                    driver.getPerspectiveCharacterSelection().show()
                }
                state.updateOrInvalidated {
                    copy(
                        availablePerspectiveCharacters = listOf(
                            AvailablePerspectiveCharacterViewModel(
                                characterId.toString(),
                                characterName,
                                true
                            )
                        )
                    )
                }
                interact {
                    driver.getPerspectiveCharacterSelection().items.find { it.text == characterName }!!.fire()
                }

                // remember that UUID, when represented as a string, will look identical.  So, make sure types are
                // correct if this test is failing
                assertEquals(
                    mapOf("characterId" to characterId.toString()),
                    viewListenerCallLog[MoralArgumentViewListener::outlineMoralArgument]
                )
            }

        }

    }


    @Nested
    inner class `When Add Section Type Selection is Opened` {

        private val characterId = UUID.randomUUID()

        @Test
        fun `should load available perspective characters`() {
            state.updateOrInvalidated {
                copy(
                    selectedPerspectiveCharacter = CharacterItemViewModel(characterId.toString(), "", ""),
                    sections = listOf(MoralArgumentSectionViewModel("", "", ""))
                )
            }

            interact {
                MoralArgumentViewDriver(moralArgumentView).getSectionTypeSelections().first().show()
            }

            assertEquals(
                mapOf<String, Any?>("characterId" to characterId.toString()),
                viewListenerCallLog[MoralArgumentViewListener::getAvailableArcSectionTypesToAdd]
            )
        }

    }

    @Nested
    inner class `Select Section Type to Add` {

        private val characterId = UUID.randomUUID()
        private val characterName = "Some Character t849tu"

        private val sectionTypeId = UUID.randomUUID()
        private val typeName = "Some Section Type"

        @Nested
        inner class `When Section Type is Unused` {

            @Test
            fun `should call view listener to add section`() {
                state.updateOrInvalidated {
                    copy(
                        selectedPerspectiveCharacter = CharacterItemViewModel(characterId.toString(), "", ""),
                        sections = List(5) { MoralArgumentSectionViewModel("$it", "", "") }
                    )
                }

                interact {
                    MoralArgumentViewDriver(moralArgumentView).getSectionTypeSelections().toList()[3].show()
                }

                state.updateOrInvalidated {
                    copy(
                        availableSectionTypes = listOf(MoralArgumentSectionTypeViewModel(sectionTypeId.toString(), typeName, true))
                    )
                }

                val item = MoralArgumentViewDriver(moralArgumentView).getSectionTypeSelections().toList()[3].items.find { it.text == typeName }!!

                interact {
                    item.fire()
                }

                assertEquals(
                    mapOf<String, Any?>("characterId" to characterId.toString(), "sectionTemplateId" to sectionTypeId.toString(), "index" to 3),
                    viewListenerCallLog[MoralArgumentViewListener::addCharacterArcSectionTypeAtIndex]
                )

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

    @AfterEach
    fun `close window`() {
        interact {
            moralArgumentView.close()
        }
    }

}