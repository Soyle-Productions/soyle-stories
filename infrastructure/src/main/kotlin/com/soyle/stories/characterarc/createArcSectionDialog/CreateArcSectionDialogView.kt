package com.soyle.stories.characterarc.createArcSectionDialog

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.labeledSection
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import javafx.beans.binding.StringBinding
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*
import tornadofx.controlsfx.popover


class CreateArcSectionDialogView : Fragment() {

    private val characterArcProperty = singleAssign<String>()
    var characterArcId: String by characterArcProperty
        private set

    private val state = resolve<CreateArcSectionDialogState>()
    private val viewListener by resolveLater<CreateArcSectionDialogViewListener>()

    override val root: Parent = vbox {
        labeledSection(state.sectionTypeSelectionFieldLabel) {
            menubutton {
                addClass(CreateArcSectionDialogStyles.sectionTypeSelection)
                disableProperty().bind(state.sectionTypeOptions.isNull)
                graphicProperty().bind(progressIndicatorWhenLoadingOptions())
                textProperty().bind(selectionText())
                items.bind(state.sectionTypeOptions) { sectionTypeMenuItem(it) }
            }
        }
        labeledSection(state.descriptionFieldLabel) {
            textarea {
                addClass(CreateArcSectionDialogStyles.description)
                disableProperty().bind(state.selectedType.isNull)
                textProperty().bindBidirectional(state.description)
            }
        }
        buttonbar {
            button(primaryButtonText()) {
                disableProperty().bind(state.selectedType.isNull)
                action { createOrModifySelectedSectionType() }
            }
        }
    }

    private fun progressIndicatorWhenLoadingOptions() = state.sectionTypeOptions.objectBinding {
        if (it == null) ProgressIndicator()
        else null
    }

    private fun selectionText(): StringBinding {
        return state.selectedType.stringBinding(state.sectionTypeSelectionNoSelectionLabel) {
            it?.sectionTypeName ?: state.sectionTypeSelectionNoSelectionLabel.valueSafe
        }
    }

    private fun sectionTypeMenuItem(option: SectionTypeOption): MenuItem {
        return when (option) {
            is SectionTypeOption.AlreadyUsed -> {
                CustomMenuItem().apply {
                    addClass(ComponentsStyles.discouragedSelection)
                    content = Label(option.sectionTypeName).apply {
                        popover { text(option.message) }
                        setOnMouseClicked { fire() }
                    }
                    action {
                        attemptToSelectSectionOption(option)
                    }
                    text = option.sectionTypeName
                }
            }
            else -> {
                MenuItem(option.sectionTypeName).apply {
                    action {
                        attemptToSelectSectionOption(option)
                    }
                }
            }
        }
    }

    private fun attemptToSelectSectionOption(option: SectionTypeOption) {
        if (changingSelectionWouldRemovedChangesToDescription(option)) {
            val alert = Alert(Alert.AlertType.CONFIRMATION, state.confirmUnsavedDescriptionChanges.valueSafe)

            alert.resultProperty().onChangeOnce {
                if (it?.buttonData == ButtonBar.ButtonData.YES) {
                    selectSectionOption(option)
                }
            }
            alert.initOwner(currentStage)
            alert.show()
        } else {
            selectSectionOption(option)
        }
    }

    private fun changingSelectionWouldRemovedChangesToDescription(nextSelection: SectionTypeOption): Boolean
    {
        val currentSelection = state.selectedType.get()
        val description = state.description.get()
        if (currentSelection is SectionTypeOption.AlreadyUsed && description != currentSelection.description) return true
        if (currentSelection !is SectionTypeOption.AlreadyUsed && description.isNotEmpty() && nextSelection is SectionTypeOption.AlreadyUsed) return true
        return false
    }

    private fun selectSectionOption(option: SectionTypeOption) {
        val currentSelection = state.selectedType.get()
        state.selectedType.set(option)
        when (option) {
            is SectionTypeOption.AlreadyUsed -> {
                state.description.set(option.description)
                state.title.set(state.modifyingExistingTitle.get())
            }
            else -> {
                if (currentSelection is SectionTypeOption.AlreadyUsed) {
                    state.description.set("")
                }
                state.title.set(state.item?.defaultTitle)
            }
        }
    }

    private fun primaryButtonText(): StringBinding
    {
        return state.selectedType.stringBinding(state.defaultPrimaryButtonLabel, state.modifyingPrimaryButtonLabel) {
            if (it is SectionTypeOption.AlreadyUsed) state.modifyingPrimaryButtonLabel.valueSafe
            else state.defaultPrimaryButtonLabel.valueSafe
        }
    }

    private fun createOrModifySelectedSectionType()
    {
        val selectedOption =  state.selectedType.value!!
        if (selectedOption is SectionTypeOption.AlreadyUsed) {
            viewListener.modifyArcSection(characterArcId, selectedOption.sectionTypeId, state.description.valueSafe)
        } else {
            viewListener.createArcSection(
                characterArcId,
                selectedOption.sectionTypeId,
                state.description.valueSafe
            )
        }
    }

    fun show(characterArcId: String) {
        if (characterArcProperty.isInitialized()) {
            throw Error("Dialog already shown.")
        }
        this.characterArcId = characterArcId
        openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
        viewListener.getValidState()
    }

    init {
        titleProperty.bind(state.title)
    }

}