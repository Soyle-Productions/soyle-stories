package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.components.*
import com.soyle.stories.common.components.asyncMenuButton.AsyncMenuButton.Companion.asyncMenuButton
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.SectionTitle.Companion.section
import com.soyle.stories.common.mapObservableTo
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel
import com.soyle.stories.theme.moralArgument.MoralArgumentInsertionPoint.Companion.insertionPoint
import com.soyle.stories.theme.moralArgument.MoralArgumentSection.Companion.moralArgumentSection
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class MoralArgumentView : View() {

    private val viewListener: MoralArgumentViewListener = resolve()
    private val state: MoralArgumentState = resolve()
    private val moralArgumentInsertionIndex = SimpleIntegerProperty(-1)

    override val root: Parent = responsiveBox {
        themeDetails {
            hgrow = Priority.ALWAYS
            moralProblemField()
            themeLineField()
            thematicRevelationField()
        }
        moralArgument {
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            perspectiveCharacterField()
            arcSections {
                moralArgumentSection(it)
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
    ) = section(labelProperty) {
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
        spacer()
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
            action {
                println("minor character selected $minorCharacter")
            }
        }
    }

    private fun unavailableCharacterMessage(minorCharacter: AvailablePerspectiveCharacterViewModel) =
        state.unavailableCharacterMessage.stringBinding {
            it?.invoke(minorCharacter)
        }

    private fun Parent.arcSections(op: VBox.(MoralArgumentSectionViewModel) -> MoralArgumentSection) {
        scrollpane {
            isFitToHeight = true
            isFitToWidth = true
            id = Styles.arcSections.name
            content = VBox().apply content@{
                isFillWidth = true
                visibleWhen(state.sections.isNotNull)
                vbox {
                    isFillWidth = true
                    state.sections.mapObservableTo(children, { it.arcSectionId }) {
                        val section = op(it).apply {
                            onSectionPlacedAbove = {
                                viewListener.moveSectionTo(it, state.item!!.selectedPerspectiveCharacter!!.characterId, root.indexInParent)
                            }
                            onSectionPlacedBelow = {
                                viewListener.moveSectionTo(it, state.item!!.selectedPerspectiveCharacter!!.characterId, root.indexInParent + 1)
                            }
                            onSectionDraggedAbove = {
                                moralArgumentInsertionIndex.set(root.indexInParent)
                            }
                            onSectionDraggedBelow = {
                                moralArgumentInsertionIndex.set(root.indexInParent + 1)
                            }
                            onSectionDraggedAway = {
                                moralArgumentInsertionIndex.set(-1)
                            }
                            onMoved = {
                                viewListener.moveSectionTo(it.arcSectionId, state.item!!.selectedPerspectiveCharacter!!.characterId, root.indexInParent)
                            }
                            onDragging = {
                                this@content.addClass(Styles.dragging)
                            }
                            onDragStop = {
                                moralArgumentInsertionIndex.set(-1)
                                this@content.removeClass(Styles.dragging)
                            }
                        }
                        section.root
                    }
                }
                insertionPoint(scope, null, tryingToInsertProperty = whenInsertingAtEnd())
            }
        }
    }

    private fun whenInsertingAtEnd() = moralArgumentInsertionIndex.booleanBinding { it == state.sections.size }

    private fun Parent.moralArgumentSection(viewModel: MoralArgumentSectionViewModel): MoralArgumentSection {
        return moralArgumentSection(scope, viewModel, state.removeSectionLabel, moralArgumentInsertionIndex)
    }

    init {
        viewListener.getValidState()
    }

    class Styles: Stylesheet() {
        companion object {
            val arcSections by cssid("arc-sections")
            val dragging by cssclass()
            init {
                importStylesheet<Styles>()
            }
        }

        init {
            arcSections {
                content {
                    and(dragging) {
                        MoralArgumentInsertionPoint.Styles.sectionTypeSelection {
                            //visibility = FXVisibility.HIDDEN
                        }
                    }
                }
            }
        }

    }
}