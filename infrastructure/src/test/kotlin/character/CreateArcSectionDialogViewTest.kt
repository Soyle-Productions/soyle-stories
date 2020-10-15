package com.soyle.stories.character

import com.soyle.stories.character.CreateArcSectionDialogViewAssert.Companion.assertThat
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.Companion.getAlert
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.Companion.getDescriptionField
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.Companion.getPrimaryButton
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.Companion.getSectionTypeSelectionField
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.DescriptionFieldAssert.Companion.assertThatDescriptionField
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.DescriptionFieldAssert.Companion.getTextInput
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.SectionTypeFieldAssert.Companion.getSelection
import com.soyle.stories.characterarc.createArcSectionDialog.*
import com.soyle.stories.common.SyncThreadTransformer
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.scene.control.*
import javafx.stage.Modality
import javafx.stage.Stage
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.FX
import tornadofx.singleAssign
import java.util.*

class CreateArcSectionDialogViewTest : ApplicationTest() {

    private val sceneId = randomizedString("scene id")
    private val themeId = randomizedString("theme id")
    private val characterId = randomizedString("character id")

    private var dialog: CreateArcSectionDialogView by singleAssign()

    @BeforeEach
    fun createDialog() {
        dialog = projectScope.get()
        interact { dialog.show() }
    }

    @Nested
    inner class Show {

        @Nested
        inner class `When Dialog First Created` {

            @Test
            fun `should call for initial state`() {
                val request = getValidStateParameters ?: error("Did not request valid state")
                assertEquals(themeId, request[0])
                assertEquals(characterId, request[1])
            }

        }

        @Nested
        inner class `When Called a Second Time` {

            @Test
            fun `should throw error`() {
                val error = assertThrows<Throwable> {
                    dialog.show()
                }
                Assertions.assertThat(error.localizedMessage).isEqualTo("Dialog already shown.")
            }

        }

        @Nested
        inner class `When Called a Second Time after being closed` {

            @BeforeEach
            fun `select and close first dialog`() {
                projectScope.get<CreateArcSectionDialogState>().update { viewModel.copy(sectionTypeOptions = listOf(SectionTypeOption("", ""))) }
                interact {
                    dialog.getSectionTypeSelectionField().getSelection().items.first().fire()
                    dialog.getDescriptionField().getTextInput().text = "Not empty"
                    dialog.close()
                }
            }

            @Test
            fun `selection and description should be clear`() {
                val newDialog = projectScope.get<CreateArcSectionDialogView>()
                interact { newDialog.show() }

                assertThat(newDialog) {
                    andSectionTypeSelectionField {
                        hasLabel(viewModel.sectionTypeSelectionNoSelectionLabel)
                    }
                    andDescriptionField {
                        hasValue("")
                    }
                }
            }

        }

    }

    @Nested
    inner class `State Update` {

        val vm = viewModel.copy(
            sectionTypeOptions = listOf(
                SectionTypeOption(randomizedString("id"), randomizedString("section type 1")),
                SectionTypeOption(randomizedString("id"), randomizedString("section type 2")),
                SectionTypeOption.AlreadyUsed(
                    randomizedString("id"),
                    randomizedString("section type 3"),
                    randomizedString("existing section id"),
                    randomizedString("section description"),
                    randomizedString("section message")
                )
            )
        )

        @Nested
        inner class `When Updated` {

            @Test
            fun `should properly display view model values`() {
                projectScope.get<CreateArcSectionDialogState>().update { vm }

                assertThat(dialog) {
                    hasTitle(vm.defaultTitle)
                    andSectionTypeSelectionField {
                        hasFieldLabel(vm.sectionTypeSelectionFieldLabel)
                        hasLabel(vm.sectionTypeSelectionNoSelectionLabel)
                        onlyHasItemsMatching(vm.sectionTypeOptions!!.map { it.sectionTypeName })
                        alreadyUsedItemsDisplayDifferently(vm.sectionTypeOptions!!.filterIsInstance<SectionTypeOption.AlreadyUsed>())
                    }
                    andDescriptionField {
                        hasFieldLabel(vm.descriptionFieldLabel)
                        isDisabled()
                    }
                    andPrimaryButton {
                        hasLabel(vm.defaultPrimaryButtonLabel)
                        isDisabled()
                    }
                }
            }

        }

    }

    @Nested
    inner class `Select Option` {

        private val unusedOption = SectionTypeOption(
            randomizedString("section type id"),
            randomizedString("section type name")
        )

        private val alreadyUsedOption = SectionTypeOption.AlreadyUsed(
            randomizedString("section type id"),
            randomizedString("section type name"),
            randomizedString("existing section id"),
            randomizedString("section type description"),
            randomizedString("section type message")
        )
        val newDescription = randomizedString("new description")

        @AfterEach
        fun `description and primary button should be enabled`() {
            assertThat(dialog) {
                andDescriptionField { isNotDisabled() }
                andPrimaryButton { isNotDisabled() }
            }
        }

        @Nested
        inner class `Select Unused Option` {

            @Nested
            inner class `When Nothing is Selected` {

                @BeforeEach
                fun updateState() {
                    projectScope.get<CreateArcSectionDialogState>()
                        .update { viewModel.copy(sectionTypeOptions = listOf(unusedOption)) }
                }

                @Test
                fun `should display selected name`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    assertThat(dialog) {
                        hasTitle(viewModel.defaultTitle)
                        andSectionTypeSelectionField { hasLabel(unusedOption.sectionTypeName) }
                        andDescriptionField { hasValue("") }
                        andPrimaryButton { hasLabel(viewModel.defaultPrimaryButtonLabel) }
                    }
                }

            }

            @Nested
            inner class `When Description has been Modified for Selected, Unused Section` {

                @BeforeEach
                fun updateState() {
                    projectScope.get<CreateArcSectionDialogState>().update {
                        viewModel.copy(
                            sectionTypeOptions = listOf(
                                unusedOption,
                                SectionTypeOption("", randomizedString("old selection name"))
                            )
                        )
                    }
                    interact {
                        dialog.getSectionTypeSelectionField().getSelection().items[1].fire()
                        dialog.getDescriptionField().getTextInput().text = newDescription
                    }
                }

                @Test
                fun `should carry description over`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    assertThat(dialog) {
                        hasTitle(viewModel.defaultTitle)
                        andSectionTypeSelectionField { hasLabel(unusedOption.sectionTypeName) }
                        andDescriptionField { hasValue(newDescription) }
                        andPrimaryButton { hasLabel(viewModel.defaultPrimaryButtonLabel) }
                    }
                }

            }

            @Nested
            inner class `When Description has been Modified for Selected, Used Section` {

                @BeforeEach
                fun updateState() {
                    projectScope.get<CreateArcSectionDialogState>().update {
                        viewModel.copy(sectionTypeOptions = listOf(unusedOption, alreadyUsedOption))
                    }
                    interact {
                        dialog.getSectionTypeSelectionField().getSelection().items[1].fire()
                        dialog.getDescriptionField().getTextInput().text = newDescription
                    }
                }

                @Test
                fun `should retain selection`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    assertThat(dialog) {
                        hasTitle(viewModel.modifyingExistingTitle)
                        andSectionTypeSelectionField { hasLabel(alreadyUsedOption.sectionTypeName) }
                        andDescriptionField { hasValue(newDescription) }
                        andPrimaryButton { hasLabel(viewModel.modifyingPrimaryButtonLabel) }
                    }
                }

                @Test
                fun `should trigger confirmation alert`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    val alert = dialog.getAlert()!!
                    assertEquals(
                        viewModel.confirmUnsavedDescriptionChanges,
                        alert.contentText
                    )
                }

            }

        }

        @Nested
        inner class `Select Used Option` {

            @Nested
            inner class `When Nothing is Selected` {

                @BeforeEach
                fun updateState() {
                    projectScope.get<CreateArcSectionDialogState>()
                        .update { viewModel.copy(sectionTypeOptions = listOf(alreadyUsedOption)) }
                }

                @Test
                fun `should display selected type name and description`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    assertThat(dialog) {
                        hasTitle(viewModel.modifyingExistingTitle)
                        andSectionTypeSelectionField { hasLabel(alreadyUsedOption.sectionTypeName) }
                        andDescriptionField { hasValue(alreadyUsedOption.description) }
                        andPrimaryButton { hasLabel(viewModel.modifyingPrimaryButtonLabel) }
                    }
                }

            }

            @Nested
            inner class `When Description has been Modified for Selected, Unused Option` {

                @BeforeEach
                fun updateState() {
                    projectScope.get<CreateArcSectionDialogState>().update {
                        viewModel.copy(sectionTypeOptions = listOf(alreadyUsedOption, unusedOption))
                    }
                    interact {
                        dialog.getSectionTypeSelectionField().getSelection().items[1].fire()
                        dialog.getDescriptionField().getTextInput().text = newDescription
                    }
                }

                @Test
                fun `should retain selection and description`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    assertThat(dialog) {
                        hasTitle(viewModel.defaultTitle)
                        andSectionTypeSelectionField { hasLabel(unusedOption.sectionTypeName) }
                        andDescriptionField { hasValue(newDescription) }
                        andPrimaryButton { hasLabel(viewModel.defaultPrimaryButtonLabel) }
                    }
                }

                @Test
                fun `should trigger confirmation alert`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    val alert = dialog.getAlert()!!
                    assertEquals(
                        viewModel.confirmUnsavedDescriptionChanges,
                        alert.contentText
                    )
                }

            }

            @Nested
            inner class `When Description has been Modified for Selected, Used Option` {

                private val otherUsedOption = SectionTypeOption.AlreadyUsed(
                    "", randomizedString("other used option name"),
                    randomizedString("existing section id"), "", ""
                )

                @BeforeEach
                fun updateState() {
                    projectScope.get<CreateArcSectionDialogState>().update {
                        viewModel.copy(sectionTypeOptions = listOf(alreadyUsedOption, otherUsedOption))
                    }
                    interact {
                        dialog.getSectionTypeSelectionField().getSelection().items[1].fire()
                        dialog.getDescriptionField().getTextInput().text = newDescription
                    }
                }

                @Test
                fun `should retain selection and description`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    assertThat(dialog) {
                        hasTitle(viewModel.modifyingExistingTitle)
                        andSectionTypeSelectionField { hasLabel(otherUsedOption.sectionTypeName) }
                        andDescriptionField { hasValue(newDescription) }
                        andPrimaryButton { hasLabel(viewModel.modifyingPrimaryButtonLabel) }
                    }
                }

                @Test
                fun `should trigger confirmation alert`() {
                    interact { dialog.getSectionTypeSelectionField().getSelection().items.first().fire() }

                    val alert = dialog.getAlert()!!
                    assertEquals(
                        viewModel.confirmUnsavedDescriptionChanges,
                        alert.contentText
                    )
                }

            }

        }

    }

    @Nested
    inner class `Description Change Alert Response` {

        private val selectedOptionAttempt = SectionTypeOption.AlreadyUsed(
            "",
            randomizedString("selectedOptionAttempt"),
            randomizedString("existing section id"),
            randomizedString("selectedOptionAttempt description"),
            ""
        )
        private val initialSelectedOption = SectionTypeOption("", randomizedString("initialSelectedOption"))
        private val newDescription = randomizedString("newDescription")

        private var alert: Alert by singleAssign()

        @BeforeEach
        fun updateState() {
            projectScope.get<CreateArcSectionDialogState>().update {
                viewModel.copy(sectionTypeOptions = listOf(selectedOptionAttempt, initialSelectedOption))
            }
            interact {
                dialog.getSectionTypeSelectionField().getSelection().items[1].fire()
                dialog.getDescriptionField().getTextInput().text = newDescription
                dialog.getSectionTypeSelectionField().getSelection().items.first().fire()
            }
            alert = dialog.getAlert()!!
        }

        @AfterEach
        fun `should have closed alert`() {
            assertFalse(alert.isShowing) { "Alert was not closed." }
            assertNull(dialog.getAlert()) { "New alert was opened." }

        }

        @Test
        fun `cancel should retain original selection and description`() {
            val targetButton = from(alert.dialogPane).lookup(".button").queryAll<Button>().find { it.text == ButtonType.CANCEL.text }!!
            interact {
                targetButton.fire()
            }

            assertThat(dialog) {
                andSectionTypeSelectionField { hasLabel(initialSelectedOption.sectionTypeName) }
                andDescriptionField { hasValue(newDescription) }
            }
        }

        @Test
        fun `confirm should update selection and description`() {
            val targetButton = from(alert.dialogPane).lookup(".button").queryAll<Button>().find { it.text == ButtonType.YES.text }!!
            interact {
                targetButton.fire()
            }

            assertThat(dialog) {
                andSectionTypeSelectionField { hasLabel(selectedOptionAttempt.sectionTypeName) }
                andDescriptionField { hasValue(selectedOptionAttempt.description) }
            }
        }

    }

    @Nested
    inner class `Primary Button Selected` {

        val newDescription = randomizedString("new description")

        @Nested
        inner class `When Unused Option is Selected` {
            val vm = viewModel.copy(
                sectionTypeOptions = listOf(
                    SectionTypeOption(randomizedString("id"), "")
                )
            )

            @BeforeEach
            fun `update state`() {
                projectScope.get<CreateArcSectionDialogState>().update { vm }
                interact {
                    dialog.getSectionTypeSelectionField().getSelection().items.first().fire()
                    dialog.getDescriptionField().getTextInput().text = newDescription
                }
            }

            @Test
            fun `should request creation`() {
                interact { dialog.getPrimaryButton().fire() }

                assertEquals(characterId, creationRequest!![0])
                assertEquals(themeId, creationRequest!![1])
                assertEquals(vm.sectionTypeOptions!!.first().sectionTypeId, creationRequest!![2])
                assertEquals(sceneId, creationRequest!![3])
                assertEquals(newDescription, creationRequest!![4])

                assertEquals(false, dialog.currentStage?.isShowing)
            }

        }

        @Nested
        inner class `When Used Option is Selected` {

            val vm = viewModel.copy(
                sectionTypeOptions = listOf(
                    SectionTypeOption.AlreadyUsed(randomizedString("id"), "",
                        randomizedString("existing section id"), "", "")
                )
            )

            @BeforeEach
            fun `update state`() {
                projectScope.get<CreateArcSectionDialogState>().update { vm }
                interact {
                    dialog.getSectionTypeSelectionField().getSelection().items.first().fire()
                    dialog.getDescriptionField().getTextInput().text = newDescription
                }
            }

            @Test
            fun `should request modification`() {
                interact { dialog.getPrimaryButton().fire() }
                
                assertEquals(characterId, modificationRequest!![0])
                assertEquals(themeId, modificationRequest!![1])
                assertEquals((vm.sectionTypeOptions!!.first() as SectionTypeOption.AlreadyUsed).existingSectionId, modificationRequest!![2])
                assertEquals(sceneId, modificationRequest!![3])
                assertEquals(newDescription, modificationRequest!![4])

                assertEquals(false, dialog.currentStage?.isShowing)
            }

        }

    }

    private fun CreateArcSectionDialogView.show() = show(
        this@CreateArcSectionDialogViewTest.characterId,
        this@CreateArcSectionDialogViewTest.themeId,
        this@CreateArcSectionDialogViewTest.sceneId
    )
    private fun randomizedString(source: String): String = "$source randomized[${UUID.randomUUID()}]"

    var getValidStateWasCalled = false
    var getValidStateParameters: List<String>? = null
    var creationRequest: List<String>? = null
    var modificationRequest: List<String>? = null
    private val projectScope = ProjectScope(
        ApplicationScope(),
        ProjectFileViewModel(UUID.randomUUID(), "", "")
    )

    private val viewModel = CreateArcSectionDialogViewModel(
        defaultTitle = randomizedString("default title"),
        modifyingExistingTitle = randomizedString("modifying existing title"),
        sectionTypeSelectionFieldLabel = randomizedString("sectionTypeSelectionFieldLabel"),
        sectionTypeSelectionNoSelectionLabel = randomizedString("sectionTypeSelectionNoSelectionLabel"),
        descriptionFieldLabel = randomizedString("descriptionFieldLabel"),
        confirmUnsavedDescriptionChanges = randomizedString("confirmUnsavedDescriptionChanges"),
        defaultPrimaryButtonLabel = randomizedString("defaultPrimaryButtonLabel"),
        modifyingPrimaryButtonLabel = randomizedString("modifyingPrimaryButtonLabel"),
        sectionTypeOptions = null
    )

    init {
        scoped<ApplicationScope> {
            provide<ThreadTransformer> { SyncThreadTransformer() }
        }
        scoped<ProjectScope> {
            provide<CreateArcSectionDialogViewListener> {
                object : CreateArcSectionDialogViewListener {
                    override fun getValidState(themeUUID: String, characterUUID: String) {
                        getValidStateParameters = listOf(themeUUID, characterUUID)
                    }

                    override fun createArcSection(
                        characterId: String,
                        themeId: String,
                        sectionTemplateId: String,
                        sceneId: String,
                        description: String
                    ) {
                        creationRequest = listOf(characterId, themeId, sectionTemplateId, sceneId, description)
                    }

                    override fun modifyArcSection(
                        characterId: String,
                        themeId: String,
                        arcSectionId: String,
                        sceneId: String,
                        description: String
                    ) {
                        modificationRequest = listOf(characterId, themeId, arcSectionId, sceneId, description)
                    }
                }
            }
        }
    }

    @BeforeEach
    fun setup() {
        FX.setPrimaryStage(FX.defaultScope, FxToolkit.registerPrimaryStage())
    }

}