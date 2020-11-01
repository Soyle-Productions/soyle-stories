package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.asyncMenuButton.AsyncMenuButton.Companion.asyncMenuButton
import com.soyle.stories.common.components.labeledSection
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.*

class MoralArgumentSection : Fragment() {

    companion object {
        fun Parent.moralArgumentSection(scope: Scope, viewModel: MoralArgumentSectionViewModel): Node {
            val moralArgumentSection =
                find<MoralArgumentSection>(scope, params = mapOf(MoralArgumentSection::viewModel to viewModel))
            this += moralArgumentSection
            return moralArgumentSection.root
        }
    }

    private val viewModel: MoralArgumentSectionViewModel by param()
    private val viewListener: MoralArgumentViewListener by resolveLater()
    private val state: MoralArgumentState by resolveLater()

    override val root: Parent = vbox {
        addSectionTypeButton()
        labeledSection(viewModel.arcSectionName) {
            id = viewModel.arcSectionId
            textfield(viewModel.arcSectionValue)
        }
    }

    private fun Region.addSectionTypeButton() {
        asyncMenuButton<MoralArgumentSectionTypeViewModel> {
            root.addClass("section-type-selection")
            root.useMaxWidth = true
            text = "Insert Section Type"
            loadingLabelProperty.bind(state.loadingSectionTypesLabel)
            sourceProperty.bind(state.availableSectionTypes)
            onLoad = ::getAvailableSectionTypesToAdd
            itemsWhenLoaded(::createSectionTypeItems)
        }
    }

    private fun getAvailableSectionTypesToAdd() {
        state.item?.selectedPerspectiveCharacter?.characterId?.let {
            viewListener.getAvailableArcSectionTypesToAdd(it)
        }
    }

    private fun createSectionTypeItems(sectionTypes: List<MoralArgumentSectionTypeViewModel>): List<MenuItem> {
        return sectionTypes.groupBy { it.canBeCreated }.withDefault { listOf() }
            .run {
                getValue(true).map(::createCreatableSectionValueTypeItem) +
                        getValue(false).map(::createUsedSectionValueTypeItem)
            }
    }

    private fun createCreatableSectionValueTypeItem(sectionType: MoralArgumentSectionTypeViewModel): MenuItem {
        return MenuItem(sectionType.sectionTypeName).apply {
            userData = sectionType
            action { addSectionType(sectionType.sectionTypeId) }
        }
    }

    private fun addSectionType(sectionTypeId: String) {
        val characterId = state.item?.selectedPerspectiveCharacter?.characterId ?: return
        viewListener.addCharacterArcSectionTypeAtIndex(characterId, sectionTypeId, state.sections.indexOf(viewModel))
    }

    private fun createUsedSectionValueTypeItem(sectionType: MoralArgumentSectionTypeViewModel): MenuItem {
        return CustomMenuItem().apply {
            text = sectionType.sectionTypeName
            addClass(ComponentsStyles.discouragedSelection)
            userData = sectionType
            content = Label(sectionType.sectionTypeName).apply {
                tooltip {
                    textProperty().bind(usedSectionMessage(sectionType))
                }
            }
        }
    }

    private fun usedSectionMessage(sectionType: MoralArgumentSectionTypeViewModel): ObservableValue<String> {
        return state.unavailableSectionTypeMessage.stringBinding { it?.invoke(sectionType) }
    }

}