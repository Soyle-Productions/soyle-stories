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

    lateinit var characterId: String
        private set
    lateinit var themeId: String
        private set
    lateinit var sceneId: String
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
            is SectionTypeOption.AlreadyUsed -> alreadyUsedSectionTypeMenuItem(option)
            else -> MenuItem()
        }.apply {
            action { attemptToSelectSectionOption(option) }
            text = option.sectionTypeName
        }
    }

    private fun alreadyUsedSectionTypeMenuItem(option: SectionTypeOption.AlreadyUsed): MenuItem {
        return CustomMenuItem().apply {
            addClass(ComponentsStyles.discouragedSelection)
            content = Label(option.sectionTypeName).apply {
                popover { text(option.message) }
                setOnMouseClicked { fire() }
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

    private fun changingSelectionWouldRemovedChangesToDescription(nextSelection: SectionTypeOption): Boolean {
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

    private fun primaryButtonText(): StringBinding {
        return state.selectedType.stringBinding(state.defaultPrimaryButtonLabel, state.modifyingPrimaryButtonLabel) {
            if (it is SectionTypeOption.AlreadyUsed) state.modifyingPrimaryButtonLabel.valueSafe
            else state.defaultPrimaryButtonLabel.valueSafe
        }
    }

    private fun createOrModifySelectedSectionType() {
        val selectedOption = state.selectedType.value!!
        if (selectedOption is SectionTypeOption.AlreadyUsed) {
            modifyArcSection(selectedOption)
        } else {
            createArcSection(selectedOption)
        }
    }

    private fun modifyArcSection(selectedOption: SectionTypeOption.AlreadyUsed) {
        viewListener.modifyArcSection(
            characterId,
            themeId,
            selectedOption.existingSectionId,
            sceneId,
            state.description.valueSafe,
        )
    }

    private fun createArcSection(selectedOption: SectionTypeOption) {
        viewListener.createArcSection(
            characterId,
            themeId,
            selectedOption.sectionTypeId,
            sceneId,
            state.description.valueSafe,
        )
    }

    private val idsInitialized = lazy { true }
    fun show(characterId: String, themeId: String, sceneId: String) {
        if (idsInitialized.isInitialized()) {
            throw Error("Dialog already shown.")
        }
        this.characterId = characterId
        this.themeId = themeId
        this.sceneId = sceneId
        idsInitialized.value
        openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
        viewListener.getValidState(themeId, characterId)
    }

    init {
        titleProperty.bind(state.title)
    }

}