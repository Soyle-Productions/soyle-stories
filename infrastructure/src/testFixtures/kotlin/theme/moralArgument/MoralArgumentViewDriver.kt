package com.soyle.stories.desktop.view.theme.moralArgument

import com.soyle.stories.common.components.Styles
import com.soyle.stories.theme.moralArgument.MoralArgumentView
import javafx.scene.Node
import javafx.scene.control.*
import org.testfx.api.FxRobot

class MoralArgumentViewDriver(private val view: MoralArgumentView) : FxRobot() {

    fun getMoralProblemFieldLabel(): Labeled {
        return from(view.root).lookup("#moral-problem-field .${Styles.fieldLabel.name}").query()
    }
    fun getMoralProblemFieldInput(): TextInputControl {
        return from(view.root).lookup("#moral-problem-field .text-field").query()
    }

    fun getThemeLineFieldLabel(): Labeled {
        return from(view.root).lookup("#theme-line-field .${Styles.fieldLabel.name}").query()
    }

    fun getThemeLineFieldInput(): TextInputControl {
        return from(view.root).lookup("#theme-line-field .text-field").query()
    }

    fun getThematicRevelationFieldLabel(): Labeled {
        return from(view.root).lookup("#thematic-revelation-field .${Styles.fieldLabel.name}").query()
    }

    fun getThematicRevelationFieldInput(): TextInputControl {
        return from(view.root).lookup("#thematic-revelation-field .text-field").query()
    }

    fun getPerspectiveCharacterLabel(): Labeled {
        return from(view.root).lookup("#perspective-character-field .${Styles.fieldLabel.name}").query()
    }

    fun getPerspectiveCharacterSelection(): MenuButton {
        return from(view.root).lookup("#perspective-character-field .menu-button").query()
    }

    private fun getArcSectionsContainer(): Node {
        return from(view.root).lookup("#arc-sections").query<ScrollPane>().content
    }

    internal fun getArcSectionNodes(): List<Node> {
        val arcSectionsContainer = getArcSectionsContainer()
        return from(arcSectionsContainer).lookup(".${Styles.labeledSection.name}").queryAll<Node>().toList()
    }

    private fun getArcSectionLabel(sectionNode: Node): Labeled = from(sectionNode).lookup(".${Styles.fieldLabel.name}").query()
    fun getArcSectionLabels(): List<Labeled> = getArcSectionNodes().map(::getArcSectionLabel)
    fun getArcSectionLabel(index: Int): Labeled = getArcSectionLabel(getArcSectionNodes()[index])

    private fun getArcSectionValueInput(sectionNode: Node): TextInputControl = from(sectionNode).lookup(".text-field").query()
    fun getArcSectionValueInputs(): List<TextInputControl> = getArcSectionNodes().map(::getArcSectionValueInput)
    fun getArcSectionValueInput(index: Int): TextInputControl = getArcSectionValueInput(getArcSectionNodes()[index])

    private fun getArcSectionDragHandle(sectionNode: Node): Node = from(sectionNode).lookup(".drag-handle").query()
    fun getArcSectionDragHandle(index: Int): Node = getArcSectionDragHandle(getArcSectionNodes()[index])

    private fun getArcSectionRemoveButton(sectionNode: Node): Button? = from(sectionNode).lookup(".remove-button").queryAll<Button>().firstOrNull()
    fun getArcSectionRemoveButton(index: Int): Button? = getArcSectionRemoveButton(getArcSectionNodes()[index])

    fun getSectionTypeSelections(): Set<MenuButton> {
        return from(getArcSectionsContainer()).lookup(".section-type-selection").queryAll()
    }

}