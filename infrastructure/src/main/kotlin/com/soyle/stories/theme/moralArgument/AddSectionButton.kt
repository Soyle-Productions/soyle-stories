package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.asyncMenuButton.AsyncMenuButton.Companion.asyncMenuButton
import com.soyle.stories.di.resolveLater
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import tornadofx.*

class AddSectionButton : Fragment() {

    companion object {
        fun Parent.addSectionButton(scope: Scope, addToTopOfSection: MoralArgumentSectionViewModel?): Node
        {
            val addSectionButton =
                find<AddSectionButton>(scope, params = mapOf(AddSectionButton::addToTopOfSection to addToTopOfSection))
            this += addSectionButton
            return addSectionButton.root
        }
    }

    private val addToTopOfSection: MoralArgumentSectionViewModel? by param()
    private val viewListener: MoralArgumentViewListener by resolveLater()
    private val state: MoralArgumentState by resolveLater()

    override val root: Parent = asyncMenuButton<MoralArgumentSectionTypeViewModel> {
        root.addClass("section-type-selection")
        root.useMaxWidth = true
        text = "Insert Section Type Here"
        loadingLabelProperty.bind(state.loadingSectionTypesLabel)
        sourceProperty.bind(state.availableSectionTypes)
        onLoad = ::getAvailableSectionTypesToAdd
        itemsWhenLoaded(::createSectionTypeItems)
    }.root

    private fun getAvailableSectionTypesToAdd() {
        state.availableSectionTypes.value = null
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
        val addIndex = addToTopOfSection?.let { addToTopOfSection ->
            state.sections.indexOf(addToTopOfSection).takeIf { it >= 0 }
        }
        if (addIndex == null) {
            viewListener.addCharacterArcSectionType(characterId, sectionTypeId)
        } else {
            viewListener.addCharacterArcSectionTypeAtIndex(characterId, sectionTypeId, addIndex)
        }
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