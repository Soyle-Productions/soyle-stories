package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.components.*
import com.soyle.stories.common.mapObservable
import com.soyle.stories.common.mapObservableTo
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel
import com.soyle.stories.theme.characterConflict.addDragAndDrop
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.MenuButton
import javafx.scene.layout.VBox
import tornadofx.*

class MoralArgumentView : View() {

    private val viewListener: MoralArgumentViewListener = resolve()
    private val state: MoralArgumentState = resolve()

    override val root: Parent = responsiveBox {
        themeDetails {
            moralProblemField()
            themeLineField()
        }
        moralArgument {
            perspectiveCharacterField()
            arcSections { moralSection ->
                labeledSection(moralSection.arcSectionName) {
                    id = moralSection.arcSectionId
                    textfield(moralSection.arcSectionValue)
                    addDragAndDrop()
                }
            }
        }
    }

    private fun Parent.themeDetails(op: Parent.() -> Unit) = vbox(op = op)
    private fun Parent.moralProblemField() = labeledSection(state.moralProblemLabel) {
        id = "moral-problem-field"
        textfield(state.moralProblemValue)
    }

    private fun Parent.themeLineField() = labeledSection(state.themeLineLabel) {
        id = "theme-line-field"
        textfield(state.themeLineValue)
    }

    private fun Parent.moralArgument(op: Parent.() -> Unit) = vbox(op = op)
    private fun Parent.perspectiveCharacterField() = hbox {
        id = "perspective-character-field"
        fieldLabel(state.perspectiveCharacterLabel)
        menubutton {
            textProperty().bind(state.perspectiveCharacterDisplay)
            addLoadingItem()
            addCharacterItemsWhenLoaded()
        }
    }

    private fun MenuButton.addLoadingItem() {
        item("") { textProperty().bind(state.loadingItemLabel) }
    }

    private fun MenuButton.addCharacterItemsWhenLoaded() {
        state.availablePerspectiveCharacters.onChange { observableList: ObservableList<AvailablePerspectiveCharacterViewModel>? ->
            items.clear()
            when {
                observableList == null || !isShowing -> addLoadingItem()
                else -> addCharacterItems(observableList)
            }
        }
    }

    private fun MenuButton.addCharacterItems(observableList: ObservableList<AvailablePerspectiveCharacterViewModel>) {
        observableList
            .groupBy { it.isMajorCharacter }
            .withDefault { listOf() }
            .apply { getValue(true).forEach { addMajorCharacterItem(it) } }
            .apply { getValue(false).forEach { addMinorCharacterItem(it) } }
    }

    private fun MenuButton.addMajorCharacterItem(it: AvailablePerspectiveCharacterViewModel) {
        item(it.characterName)
    }

    private fun MenuButton.addMinorCharacterItem(minorCharacter: AvailablePerspectiveCharacterViewModel) {
        customitem {
            userData = minorCharacter
            text = minorCharacter.characterName
            addClass(ComponentsStyles.discouragedSelection)
            content = Label(minorCharacter.characterName).apply {
                tooltip {
                    textProperty().bind(unavailableCharacterMessage(minorCharacter))
                }
            }
        }
    }

    private fun unavailableCharacterMessage(minorCharacter: AvailablePerspectiveCharacterViewModel) =
        state.unavailableCharacterMessage.stringBinding {
            it?.invoke(minorCharacter)
        }

    private fun Parent.arcSections(op: VBox.(MoralArgumentSectionViewModel) -> Node) {
        scrollpane(fitToWidth = true) {
            id = "arc-sections"
            content = vbox {
                state.sections.mapObservableTo(children, {it.arcSectionId}) { op(it) }
            }
        }
    }

    init {
        viewListener.getValidState()
    }
}