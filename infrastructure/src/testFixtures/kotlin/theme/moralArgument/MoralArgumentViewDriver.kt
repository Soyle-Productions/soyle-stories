package com.soyle.stories.desktop.view.theme.moralArgument

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.Styles
import com.soyle.stories.theme.moralArgument.MoralArgumentView
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
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

    fun getPerspectiveCharacterLabel(): Labeled {
        return from(view.root).lookup("#perspective-character-field .${Styles.fieldLabel.name}").query()
    }

    fun getPerspectiveCharacterSelection(): MenuButton {
        return from(view.root).lookup("#perspective-character-field .menu-button").query()
    }

    private fun getArcSectionsContainer(): Node {
        return from(view.root).lookup("#arc-sections").query<ScrollPane>().content
    }

    fun getArcSectionNodes(): List<Node> {
        val arcSectionsContainer = getArcSectionsContainer()
        return from(arcSectionsContainer).lookup(".${Styles.labeledSection.name}").queryAll<Node>().toList()
    }

    fun getArcSectionLabels(): List<Labeled> {
        return getArcSectionNodes().map {
            from(it).lookup(".${Styles.fieldLabel.name}").query()
        }
    }

    fun getArcSectionValues(): List<TextInputControl> {
        return getArcSectionNodes().map {
            from(it).lookup(".text-field").query()
        }
    }

    fun getSectionTypeSelections(): Set<MenuButton> {
        return from(getArcSectionsContainer()).lookup(".section-type-selection").queryAll()
    }

}