package com.soyle.stories.characterarc.createArcSectionDialog

import com.soyle.stories.character.createArcSection.CreatedCharacterArcSectionNotifier
import com.soyle.stories.character.createArcSection.CreatedCharacterArcSectionReceiver
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.text.SectionTitle.Companion.section
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.StageStyle
import kotlinx.coroutines.Job
import tornadofx.*
import tornadofx.controlsfx.popover


class CreateArcSectionDialogView : Fragment() {

    companion object {
        fun createArcSectionDialog(scope: ProjectScope, characterId: Character.Id, themeId: Theme.Id, onCreateArcSection: ((CharacterArcSection.Id, String) -> Unit)? = null) {
            val dialog = find<CreateArcSectionDialogView>(scope = scope, params = mapOf(
                "characterId" to characterId,
                "themeId" to themeId,
                "onCreateArcSection" to onCreateArcSection
            ))
            dialog.show()
        }
    }

    override val scope: ProjectScope = super.scope as ProjectScope

    private val characterId: Character.Id by params
    private val themeId: Theme.Id by params
    private val onCreateArcSection: ((CharacterArcSection.Id, String) -> Unit)? by params

    private val state = resolve<CreateArcSectionDialogState>()
    private val viewListener by resolveLater<CreateArcSectionDialogViewListener>()

    private val selectedType = SimpleObjectProperty<SectionTypeOption?>(null)
    private val description = SimpleStringProperty("")

    override val root: Parent = vbox {
        addClass(ComponentsStyles.cardBody)
        section(state.sectionTypeSelectionFieldLabel) {
            menubutton {
                addClass(CreateArcSectionDialogStyles.sectionTypeSelection)
                disableProperty().bind(state.sectionTypeOptions.isNull)
                graphicProperty().bind(progressIndicatorWhenLoadingOptions())
                textProperty().bind(selectionText())
                items.bind(state.sectionTypeOptions) { sectionTypeMenuItem(it) }
            }
        }
        section(state.descriptionFieldLabel) {
            textarea {
                addClass(CreateArcSectionDialogStyles.description)
                disableProperty().bind(selectedType.isNull)
                textProperty().bindBidirectional(description)
            }
        }
        buttonbar {
            button(primaryButtonText()) {
                disableProperty().bind(selectedType.isNull)
                action { createOrModifySelectedSectionType() }
            }
        }
    }

    private fun progressIndicatorWhenLoadingOptions() = state.sectionTypeOptions.objectBinding {
        if (it == null) ProgressIndicator()
        else null
    }

    private fun selectionText(): StringBinding {
        return selectedType.stringBinding(state.sectionTypeSelectionNoSelectionLabel) {
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
                tooltip {
                    showDelay = 0.seconds
                    text = option.message
                }
            }
        }
    }

    private fun attemptToSelectSectionOption(option: SectionTypeOption) {
        if (changingSelectionWouldRemovedChangesToDescription(option)) {
            val alert = Alert(Alert.AlertType.CONFIRMATION, state.confirmUnsavedDescriptionChanges.valueSafe, ButtonType.YES, ButtonType.CANCEL)

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
        val currentSelection = selectedType.get()
        val description = description.get()
        if (currentSelection is SectionTypeOption.AlreadyUsed && description != currentSelection.description) return true
        if (currentSelection !is SectionTypeOption.AlreadyUsed && description.isNotEmpty() && nextSelection is SectionTypeOption.AlreadyUsed) return true
        return false
    }

    private fun selectSectionOption(option: SectionTypeOption) {
        val currentSelection = selectedType.get()
        selectedType.set(option)
        when (option) {
            is SectionTypeOption.AlreadyUsed -> {
                description.set(option.description)
                state.title.set(state.modifyingExistingTitle.get())
            }
            else -> {
                if (currentSelection is SectionTypeOption.AlreadyUsed) {
                    description.set("")
                }
                state.title.set(state.item?.defaultTitle)
            }
        }
    }

    private fun primaryButtonText(): StringBinding {
        return selectedType.stringBinding(state.defaultPrimaryButtonLabel, state.modifyingPrimaryButtonLabel) {
            if (it is SectionTypeOption.AlreadyUsed) state.modifyingPrimaryButtonLabel.valueSafe
            else state.defaultPrimaryButtonLabel.valueSafe
        }
    }

    private fun createOrModifySelectedSectionType() {
        val selectedOption = selectedType.value!!
        if (selectedOption is SectionTypeOption.AlreadyUsed) {
            modifyArcSection(selectedOption)
        } else {
            createArcSection(selectedOption)
        }
    }

    private fun modifyArcSection(selectedOption: SectionTypeOption.AlreadyUsed) {
        val onCreateArcSection = this.onCreateArcSection
        if (onCreateArcSection == null) {
            viewListener.modifyArcSection(
                characterId,
                themeId,
                selectedOption.existingSectionId,
                description.valueSafe,
            )
        } else {
            viewListener.modifyArcSection(
                characterId,
                themeId,
                selectedOption.existingSectionId,
                description.valueSafe,
            )
        }
        close()
    }

    private fun createArcSection(selectedOption: SectionTypeOption) {
        val onCreateArcSection = this.onCreateArcSection
        if (onCreateArcSection == null) {
            viewListener.createArcSection(
                characterId,
                themeId,
                selectedOption.sectionTypeId,
                description.valueSafe,
            )
        } else {
            listenForCreatedArcSection(onCreateArcSection)
            viewListener.createArcSection(
                characterId,
                themeId,
                selectedOption.sectionTypeId,
                description.valueSafe,
            )
        }
    }

    private fun listenForCreatedArcSection(onCreateArcSection: (CharacterArcSection.Id, String) -> Unit) {
        val notifier = scope.get<CreatedCharacterArcSectionNotifier>()
        notifier.addListener(object : CreatedCharacterArcSectionReceiver {
            override suspend fun receiveCreatedCharacterArcSection(event: ArcSectionAddedToCharacterArc) {
                notifier.removeListener(this)
                onCreateArcSection(
                    CharacterArcSection.Id(event.characterArcSectionId),
                    description.valueSafe
                )
                close()
            }
        })
    }

    private fun show() {
        state.item = null
        viewListener.getValidState(themeId, characterId)
        val stage = openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
        state.done.onChangeUntil({ stage?.isShowing != true }) {
            if (it == true) close()
        }
    }

    init {
        titleProperty.bind(state.title)
    }

}