package com.soyle.stories.scene.characters.inspect

import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.secondaryButton
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.elevation
import com.soyle.stories.common.components.surfaces.elevationVariant
import com.soyle.stories.common.components.surfaces.outlined
import com.soyle.stories.common.components.text.SectionTitle.Companion.section
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemStyles
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionStyles as Styles
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.showPopover
import kotlin.collections.set

fun CharacterInSceneInspection(
    viewModel: CharacterInSceneInspectionViewModel,
    header: Node
): Node {
    return CharacterInSceneInspectionView(
        viewModel, header
    )
}

@ViewBuilder
fun Parent.characterInSceneInspection(
    viewModel: CharacterInSceneInspectionViewModel,
    header: Node
): Node {
    return CharacterInSceneInspectionView(
        viewModel, header
    ).also { add(it) }
}

private class CharacterInSceneInspectionView(
    override val viewModel: CharacterInSceneInspectionViewModel,
    header: Node
) : VBox(), ViewOf<CharacterInSceneInspectionViewModel> {

    init {
        addClass(Styles.characterEditor)
        disableWhen(viewModel.isLoading)

        add(header)

        vbox {
            addClass(Styles.editorBody)
            vgrow = Priority.ALWAYS

            vbox {
                addClass(Styles.roleInSceneSelection)
                incitingCharacterToggle()
                opponentCharacterToggle()
            }

            desireField()
            motivationField()
        }

        hbox {
            addClass(Styles.editorFooter)
            elevation = Elevation.getValue(24)
            elevationVariant = outlined
            secondaryButton("Remove from Scene", variant = null) {
                id = "remove-from-scene"
                action(viewModel.onRemoveCharacter)
            }
        }
    }

    @ViewBuilder
    private fun VBox.incitingCharacterToggle() {
        radiobutton("Is Inciting Character") {
            id = Styles.incitingCharacter.name
            scopedListener(viewModel.role()) { isSelected = viewModel.hasRole(RoleInScene.IncitingCharacter) }
            action { viewModel.onToggleRole(RoleInScene.IncitingCharacter) }
        }
    }

    @ViewBuilder
    private fun VBox.opponentCharacterToggle() {
        radiobutton("Is Opponent to Inciting Character") {
            id = Styles.opponentCharacter.name
            scopedListener(viewModel.role()) { isSelected = viewModel.hasRole(RoleInScene.OpponentCharacter) }
            action { viewModel.onToggleRole(RoleInScene.OpponentCharacter) }
        }
    }

    @ViewBuilder
    private fun VBox.desireField() {
        section("Desire in Scene") {
            addClass("desire")
            textfield {
                id = Styles.desire.name
                scopedListener(viewModel.desire()) { text = it }
                onLoseFocus { viewModel.onDesireChanged(text.orEmpty()) }
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
            id = Styles.motivation.name
            scopedListener(viewModel.motivation()) { text = it }
            scopedListener(viewModel.inheritedMotivation()) { promptText = it?.motivation }
            onLoseFocus { viewModel.onMotivationChanged(text.orEmpty()) }
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
            existsWhen(viewModel.motivation().isNotEmpty)
            action(viewModel.onResetToPreviousMotivation)
        }
    }

    @ViewBuilder
    private fun HBox.lastSetSource() {
        hyperlink("When was this last set?") {
            addClass(Styles.lastSetSource)
            action { showPopover() }
            scopedListener(viewModel.inheritedMotivation()) {
                println("inheritedMotivation for ${viewModel.itemViewModel.name}\n  is: ${it?.motivation}")
                exists = it != null
                previousMotivationPopover(it)
            }
        }
    }

    @ViewBuilder
    private fun Node.previousMotivationPopover(previousMotivation: InheritedMotivationViewModel?) {
        if (previousMotivation != null) {
            popover {
                vbox {
                    hyperlink(previousMotivation.sceneName()) {
                        id = Styles.previousMotivationSource.name
                        action { viewModel.onNavigateToPreviousScene(previousMotivation.sceneId) }
                    }
                    label(previousMotivation.motivation()) {
                        id = Styles.previousMotivation.name
                    }
                }
            }
        } else {
            properties["popOver"] = null
        }
    }

}

class CharacterInSceneInspectionStyles : Stylesheet() {
    companion object {
        val characterEditor by cssclass()
        val editorBody by cssclass()
        val editorFooter by cssclass()
        val roleInSceneSelection by cssclass()

        val motivation by cssid()
        val desire by cssid()
        val incitingCharacter by cssid()
        val opponentCharacter by cssid()

        val lastSetSource by cssclass()

        val previousMotivationSource by cssid()
        val previousMotivation by cssid()

        init {
            styleImporter<Styles>()
        }
    }

    init {
        characterEditor {
            padding = box(3.px, 0.px, 0.px, 0.px)
            CharacterInSceneItemStyles.characterInSceneItem {
                padding = box(8.px, 16.px)
            }
        }
        editorBody {
            padding = box(16.px)
            spacing = 16.px
        }
        editorFooter {
            padding = box(8.px)
            alignment = Pos.CENTER_RIGHT
        }
        roleInSceneSelection {
            spacing = 8.px
        }
    }
}