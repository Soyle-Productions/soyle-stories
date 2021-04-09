package com.soyle.stories.scene.sceneCharacters.characterEditor

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.ComponentsStyles.Companion.card
import com.soyle.stories.common.components.ComponentsStyles.Companion.lifted
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.components.buttons.primaryMenuButton
import com.soyle.stories.common.components.buttons.secondaryButton
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Companion.chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipDeleteIcon
import com.soyle.stories.common.components.text.SectionTitle.Companion.section
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.exists
import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.sceneCharacters.*
import com.soyle.stories.scene.sceneCharacters.controlledBy
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemView
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemView.Companion.includedCharacterItem
import com.soyle.stories.soylestories.Styles
import javafx.animation.Interpolator
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.SnapshotParameters
import javafx.scene.control.*
import javafx.scene.image.WritableImage
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.util.Duration
import tornadofx.*
import tornadofx.Stylesheet.Companion.menuButton
import tornadofx.controlsfx.popover
import java.lang.ref.WeakReference
import javax.swing.text.html.ImageView
import kotlin.time.milliseconds

class SelectedSceneCharacterEditor : View() {

    companion object {
        @ViewBuilder
        fun Parent.selectedSceneCharacterEditor(scope: ProjectScope, characterBeingEdited: ObjectProperty<IncludedCharacterViewModel?>) {
            val model = find<CharacterEditorModel>(scope)
            characterBeingEdited.onChange { model.item = it }
            find<SelectedSceneCharacterEditor>(scope).addTo(this)
        }
    }

    override val scope: ProjectScope = super.scope as ProjectScope

    private val props: CharacterEditorModel = resolve()
    private val logic = SelectedSceneCharacterEditorLogic(this, props)

    override val root: Parent = vbox {
        addClass(Styles.characterEditor)
        controlledBy(logic::characterEditor)
        includedCharacterItem(props.itemProperty()) {
            actionText.set("DONE")
            action.set(logic::closeEditor)
            actionButton {
                addClass(ComponentsStyles.primary)
                addClass(ComponentsStyles.filled)
            }
        }

        vbox {
            addClass(Styles.editorBody)
            controlledBy(logic::editorBody)
            vgrow = Priority.ALWAYS

            vbox {
                addClass(Styles.roleInSceneSelection)
                incitingCharacterToggle()
                opponentCharacterToggle()
            }

            desireField()
            motivationField()

            vbox {
                hbox {
                    sectionTitle("Position on Arc")
                    spacer()
                    coverArcSectionButton()
                }
                flowpane {
                    addClass(Styles.coveredArcSectionChips)
                    dynamicContent(props.coveredArcSections) { coveredArcSections ->
                        coveredArcSections?.forEach {
                            coveredArcSectionChip(it)
                        }
                    }
                }
            }
        }

        hbox {
            addClass(Styles.editorFooter)
            controlledBy(logic::editorFooter)
            secondaryButton("Remove from Scene", variant = null) {
                id = "remove-from-scene"
                controlledBy(logic::removeCharacterButton)
            }
        }
    }

    @ViewBuilder
    private fun Parent.coveredArcSectionChip(it: CoveredArcSectionViewModel) {
        chip(
            text = it.displayLabel,
            color = Chip.Color.Secondary,
            variant = Chip.Variant.Outlined
        ) {
            id = it.arcSectionId.uuid.toString()
            addClass(Styles.coveredArcSectionChip)
            controlledBy(logic::coveredArcSection)
        }
    }

    @ViewBuilder
    private fun Parent.coverArcSectionButton() {
        menubutton("Add Section") {
            id = "coverArcSectionButton"
            addClass(ComponentsStyles.primary)
            addClass(ComponentsStyles.outlined)
            controlledBy(logic::coverArcSectionButton)
            items.bindToAvailableSectionsToCover()
        }
    }

    @ViewBuilder
    private fun ObservableList<MenuItem>.bindToAvailableSectionsToCover() {
        props.availableCharacterArcSections.onChangeWithCurrent {
            when {
                it == null -> setAll(loadingItem())
                it.isEmpty() -> setAll(createCharacterArcOption(), SeparatorMenuItem(), allCharacterArcsUsedMessage())
                else -> setAll(createCharacterArcOption(), SeparatorMenuItem(), *(it.map(::availableArcOption)).toTypedArray())
            }
        }
    }

    private fun loadingItem(): MenuItem = MenuItem("Loading ... ").apply { isDisable = true }
    private fun allCharacterArcsUsedMessage(): MenuItem =
        MenuItem().apply {
            textProperty().bind(props.itemProperty().stringBinding {
                "${it?.name} has no character arcs yet"
            })
            isDisable = true }

    private fun createCharacterArcOption(): MenuItem = MenuItem("Create New Character Arc").apply {
        controlledBy(logic::createCharacterArcOption)
    }

    private fun availableArcOption(availableArc: AvailableCharacterArcViewModel): MenuItem {
        return Menu(
            availableArc.characterArcName,
            availableArcCoveredSectionCounter(availableArc.numberOfCoveredSections, availableArc.sections.size),
            *(listOf(createArcSectionOption(availableArc.themeId), SeparatorMenuItem()) + availableArc.sections.map(::availableArcSectionOption)).toTypedArray()
        )
    }

    private fun availableArcCoveredSectionCounter(coveredSectionCount: Int, totalSectionCount: Int): Node {
        return label(coveredSectionCount.toString())
    }

    private fun createArcSectionOption(themeId: Theme.Id): MenuItem
    {
        return MenuItem("Create New Character Arc Section").controlledBy(logic.createArcSectionOption(themeId))
    }

    private fun availableArcSectionOption(availableSection: AvailableArcSectionViewModel): MenuItem {
        return CheckMenuItem(availableSection.arcSectionLabel).apply {
            userData = availableSection
            isSelected = availableSection.isCovered
            controlledBy(logic::availableArcSectionOption)
        }
    }

    @ViewBuilder
    private fun VBox.incitingCharacterToggle() {
        radiobutton("Is Inciting Character") {
            selectedProperty().bindBidirectional(props.isIncitingCharacter)
            controlledBy(logic::incitingCharacterToggle)
        }
    }

    @ViewBuilder
    private fun VBox.opponentCharacterToggle() {
        checkbox("Is Opponent to Inciting Character") {
            selectedProperty().bindBidirectional(props.isOpponentCharacter)
            controlledBy(logic::opponentCharacterToggle)
        }
    }

    @ViewBuilder
    private fun VBox.desireField() {
        section("Desire in Scene") {
            addClass("desire")
            textfield {
                textProperty().bindBidirectional(props.desire)
                controlledBy(logic::desire)
            }
        }
    }

    @ViewBuilder
    private fun VBox.motivationField() {
        section("Motivation in Scene") {
            addClass("motivation")
            motivation()
            previousMotivationButtons()
        }
    }

    @ViewBuilder
    private fun VBox.motivation() {
        textfield {
            textProperty().bindBidirectional(props.motivation)
            promptTextProperty().bind(props.previousMotivationValue)
            controlledBy(logic::motivation)
        }
    }

    @ViewBuilder
    private fun VBox.previousMotivationButtons() {
        hbox {
            resetToPreviousValue()
            spacer()
            lastSetSource()
        }
    }

    @ViewBuilder
    private fun HBox.resetToPreviousValue() {
        hyperlink("Reset to Previous Value") {
            existsWhen(props.motivation.isNotNull)
            controlledBy(logic::resetToPreviousMotivationButton)
        }
    }

    @ViewBuilder
    private fun HBox.lastSetSource() {
        hyperlink("When was this last set?") {
            existsWhen(props.hasPreviousMotivation)
            popover {
                hyperlink(props.previousMotivationSourceName).controlledBy(logic::previousMotivationLink)
            }
            action { popover?.show(this) }
        }
    }

    @ViewBuilder
    fun addTo(parent: Parent) {
        parent.addChildIfPossible(root)
    }

    class Styles : Stylesheet() {
        companion object {
            val characterEditor by cssclass()
            val editorBody by cssclass()
            val editorFooter by cssclass()
            val roleInSceneSelection by cssclass()
            val coveredArcSectionChips by cssclass()
            val coveredArcSectionChip by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            characterEditor {
                padding = box(3.px, 0.px, 0.px, 0.px)
                +lifted

                IncludedCharacterItemView.Styles.includedCharacterItem {
                    padding = box(8.px, 16.px)
                    backgroundColor = multi(Color.WHITE)
                }
            }
            editorBody {
                backgroundColor = multi(Color.WHITE)
                padding = box(16.px)
                spacing = 16.px
            }
            editorFooter {
                backgroundColor = multi(Color.WHITE)
                padding = box(8.px)
                borderWidth = multi(box(1.px, 0.px, 0.px, 0.px))
                borderColor = multi(box(Color.LIGHTGREY, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT))
                alignment = Pos.CENTER_RIGHT
            }
            roleInSceneSelection {
                spacing = 8.px
            }
            coveredArcSectionChips {
                vgap = 8.px
                hgap = 8.px
                padding = box(8.px)

                chip {
                    label {
                        textOverrun = OverrunStyle.CENTER_WORD_ELLIPSIS
                    }
                }
            }
        }
    }

}