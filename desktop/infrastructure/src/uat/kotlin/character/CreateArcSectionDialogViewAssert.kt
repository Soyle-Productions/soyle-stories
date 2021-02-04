package com.soyle.stories.character

import com.soyle.stories.character.CreateArcSectionDialogViewAssert.DescriptionFieldAssert.Companion.assertThatDescriptionField
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.DescriptionFieldAssert.Companion.getTextInput
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.PrimaryButtonAssert.Companion.assertThatPrimaryButton
import com.soyle.stories.character.CreateArcSectionDialogViewAssert.SectionTypeFieldAssert.Companion.assertThatSectionTypeSelectionField
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.characterarc.createArcSectionDialog.SectionTypeOption
import com.soyle.stories.common.components.ComponentsStyles
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.controlsfx.popover
import tornadofx.hasClass

class CreateArcSectionDialogViewAssert private constructor(private val dialog: CreateArcSectionDialogView) :
    AbstractAssert<CreateArcSectionDialogViewAssert, CreateArcSectionDialogView>(
        dialog,
        CreateArcSectionDialogViewAssert::class.java
    ) {

    fun hasTitle(expectedTitle: String) {
        Assertions.assertThat(dialog.title)
            .overridingErrorMessage("Dialog title expected to be $expectedTitle, but was ${dialog.title}")
            .isEqualTo(expectedTitle)
    }

    fun andSectionTypeSelectionField(assertions: SectionTypeFieldAssert.() -> Unit) {
        assertThatSectionTypeSelectionField(dialog.getSectionTypeSelectionField(), assertions)
    }

    fun andDescriptionField(assertions: DescriptionFieldAssert.() -> Unit) {
        assertThatDescriptionField(dialog.getDescriptionField(), assertions)
    }

    fun andPrimaryButton(assertions: PrimaryButtonAssert.() -> Unit) {
        assertThatPrimaryButton(dialog.getPrimaryButton(), assertions)
    }

    class SectionTypeFieldAssert private constructor(private val field: VBox) :
        AbstractAssert<SectionTypeFieldAssert, VBox>(field, SectionTypeFieldAssert::class.java) {

        fun hasFieldLabel(expectedFieldLabel: String) {
            Assertions.assertThat(field.getLabel().text)
                .isEqualTo(expectedFieldLabel)
        }

        fun hasLabel(expectedLabel: String) {
            val value = field.getSelection().text
            Assertions.assertThat(value)
                .overridingErrorMessage("Section Type Selection text expected to be $expectedLabel, but was $value")
                .isEqualTo(expectedLabel)
        }

        fun onlyHasItemsMatching(expectedItemLabels: List<String>) {
            Assertions.assertThat(field.getSelection().items.map { it.text }.toSet())
                .isEqualTo(expectedItemLabels.toSet())
        }

        fun alreadyUsedItemsDisplayDifferently(expectedAlreadyUsedOptions: List<SectionTypeOption.AlreadyUsed>) {
            val itemsByText = field.getSelection().items.associateBy { it.text }
            expectedAlreadyUsedOptions.forEach { option ->
                val item = itemsByText.getValue(option.sectionTypeName)
                assertTrue(item.hasClass(ComponentsStyles.discouragedSelection))
                assertTrue(
                    ((item as CustomMenuItem).content as Label).tooltip!!.text.isNotBlank()
                )
            }
        }

        companion object {
            fun assertThatSectionTypeSelectionField(field: VBox, assertions: SectionTypeFieldAssert.() -> Unit = {}) {
                SectionTypeFieldAssert(field).assertions()
            }

            fun VBox.getLabel(): Label {
                return ((children.first() as HBox).children.first() as Label)
            }

            fun VBox.getSelection(): MenuButton {
                return children.component2() as MenuButton
            }
        }

    }

    class DescriptionFieldAssert private constructor(private val field: VBox) :
        AbstractAssert<SectionTypeFieldAssert, VBox>(field, DescriptionFieldAssert::class.java) {

        fun hasFieldLabel(expectedFieldLabel: String) {
            Assertions.assertThat(field.getLabel().text)
                .isEqualTo(expectedFieldLabel)
        }

        fun hasValue(expectedValue: String) {
            val value = field.getTextInput().text
            Assertions.assertThat(value)
                .overridingErrorMessage("Description value should be <$expectedValue>, but was <$value>")
                .isEqualTo(expectedValue)
        }

        fun isDisabled() = Assertions.assertThat(field.getTextInput().isDisable)
            .overridingErrorMessage("Description should be disabled").isTrue

        fun isNotDisabled() = Assertions.assertThat(field.getTextInput().isDisable)
            .overridingErrorMessage("Description should not be disabled").isFalse

        companion object {

            fun assertThatDescriptionField(field: VBox, assertions: DescriptionFieldAssert.() -> Unit = {}) {
                DescriptionFieldAssert(field).assertions()
            }

            fun VBox.getLabel(): Label {
                return ((children.first() as HBox).children.first() as Label)
            }

            fun VBox.getTextInput(): TextArea {
                return children.component2() as TextArea
            }
        }

    }

    class PrimaryButtonAssert private constructor(private val button: Button) {

        fun hasLabel(expectedLabel: String)
        {
            val value = button.text
            Assertions.assertThat(value)
                .overridingErrorMessage("Primary Button text should be <$expectedLabel>, but was <$value>")
                .isEqualTo(expectedLabel)
        }

        fun isDisabled() = Assertions.assertThat(button.isDisable)
            .overridingErrorMessage("Primary Button should be disabled").isTrue

        fun isNotDisabled() = Assertions.assertThat(button.isDisable)
            .overridingErrorMessage("Primary Button should not be disabled").isFalse

        companion object {
            fun assertThatPrimaryButton(button: Button, assertions: PrimaryButtonAssert.() -> Unit = {}) {
                PrimaryButtonAssert(button).assertions()
            }
        }

    }

    companion object : ApplicationTest() {
        fun assertThat(
            dialog: CreateArcSectionDialogView,
            assertions: CreateArcSectionDialogViewAssert.() -> Unit = {}
        ): CreateArcSectionDialogViewAssert {
            return CreateArcSectionDialogViewAssert(dialog).also(assertions)
        }

        fun CreateArcSectionDialogView.getSectionTypeSelectionField(): VBox {
            return from(root)
                .lookup("." + com.soyle.stories.common.components.Styles.labeledSection.name)
                .queryAll<VBox>()
                .toList()
                .component1()
        }

        fun CreateArcSectionDialogView.getDescriptionField(): VBox {
            return from(root)
                .lookup("." + com.soyle.stories.common.components.Styles.labeledSection.name)
                .queryAll<VBox>()
                .toList()
                .component2()
        }

        fun CreateArcSectionDialogView.getPrimaryButton(): Button {
            return from(root).lookup(".button-bar .button").queryAll<Button>().first()
        }

        fun CreateArcSectionDialogView.getAlert(): Alert? {
            val stage = listWindows()
                .asSequence()
                .filterIsInstance<Stage>()
                .firstOrNull {
                    it.modality == Modality.APPLICATION_MODAL && it.isShowing && it.owner == currentStage
                }
            return (stage?.scene?.root as? DialogPane)?.run {
                this::class.java.getDeclaredField("dialog").let {
                    it.isAccessible = true
                    try {
                        it.get(this) as Alert
                    } catch (t: Throwable) {
                        null
                    }
                }
            }
        }

    }

}