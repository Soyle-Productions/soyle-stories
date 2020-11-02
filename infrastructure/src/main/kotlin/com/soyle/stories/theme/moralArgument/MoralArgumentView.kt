package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.components.*
import com.soyle.stories.common.components.asyncMenuButton.AsyncMenuButton.Companion.asyncMenuButton
import com.soyle.stories.common.mapObservable
import com.soyle.stories.common.mapObservableTo
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.soylestories.SplashScreen
import com.soyle.stories.soylestories.Styles
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel
import com.soyle.stories.theme.characterConflict.addDragAndDrop
import com.soyle.stories.theme.moralArgument.AddSectionButton.Companion.addSectionButton
import com.soyle.stories.theme.moralArgument.MoralArgumentSection.Companion.moralArgumentSection
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.PathElement
import tornadofx.*

class MoralArgumentView : View() {

    private val viewListener: MoralArgumentViewListener = resolve()
    private val state: MoralArgumentState = resolve()

    override val root: Parent = responsiveBox {
        themeDetails {
            hgrow = Priority.ALWAYS
            moralProblemField()
            themeLineField()
            thematicRevelationField()
        }
        moralArgument {
            hgrow = Priority.ALWAYS
            perspectiveCharacterField()
            arcSections {
                moralArgumentSection(scope, it)
            }
        }
    }

    private fun Parent.themeDetails(op: Parent.() -> Unit) = vbox(op = op)
    
    private fun Parent.moralProblemField() = moralArgumentFrameField(
        labelProperty = state.moralProblemLabel,
        valueProperty = state.moralProblemValue,
        onChange = viewListener::setMoralProblem
    ).apply { id = "moral-problem-field" }

    private fun Parent.themeLineField() = moralArgumentFrameField(
        labelProperty = state.themeLineLabel,
        valueProperty = state.themeLineValue,
        onChange = viewListener::setThemeLine
    ).apply { id = "theme-line-field" }

    private fun Parent.thematicRevelationField() = moralArgumentFrameField(
        labelProperty = state.thematicRevelationLabel,
        valueProperty = state.thematicRevelationValue,
        onChange = viewListener::setThematicRevelation
    ).apply { id = "thematic-revelation-field" }

    private fun Parent.moralArgumentFrameField(
        labelProperty: ObservableValue<String>,
        valueProperty: ObservableValue<String>,
        onChange: (String) -> Unit
    ) = labeledSection(labelProperty) {
        textfield {
            text = valueProperty.value ?: ""
            valueProperty.onChange { text = it }
            onLoseFocus {
                if (valueProperty.value != text) {
                    onChange(text)
                }
            }
        }
    }

    private fun Parent.moralArgument(op: Parent.() -> Unit) = vbox(op = op)

    private fun Parent.perspectiveCharacterField() = hbox {
        id = "perspective-character-field"
        fieldLabel(state.perspectiveCharacterLabel)
        asyncMenuButton<AvailablePerspectiveCharacterViewModel> {
            textProperty.bind(state.perspectiveCharacterDisplay)
            loadingLabelProperty.bind(state.loadingCharacterLabel)
            sourceProperty.bind(state.availablePerspectiveCharacters)
            onLoad = viewListener::getPerspectiveCharacters
            itemsWhenLoaded(::createCharacterItems)
        }
    }

    private fun createCharacterItems(list: List<AvailablePerspectiveCharacterViewModel>): List<MenuItem> {
        return list.groupBy { it.isMajorCharacter }
            .withDefault { listOf() }
            .run {
                getValue(true).map { createMajorCharacterItem(it) } +
                getValue(false).map { createMinorCharacterItem(it) }
            }
    }

    private fun createMajorCharacterItem(it: AvailablePerspectiveCharacterViewModel): MenuItem {
        return MenuItem(it.characterName).apply {
            action { viewListener.outlineMoralArgument(it.characterId) }
        }
    }

    private fun createMinorCharacterItem(minorCharacter: AvailablePerspectiveCharacterViewModel): MenuItem {
        return CustomMenuItem().apply {
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
                vbox {
                    state.sections.mapObservableTo(children, { it.arcSectionId }) {
                        op(it)
                    }
                }
                addSectionButton(scope, null)
            }
        }
    }

    init {
        viewListener.getValidState()
    }
}