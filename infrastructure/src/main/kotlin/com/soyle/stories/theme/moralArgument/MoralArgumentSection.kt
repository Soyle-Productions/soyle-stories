package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.asyncMenuButton.AsyncMenuButton.Companion.asyncMenuButton
import com.soyle.stories.common.components.labeledSection
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.theme.moralArgument.AddSectionButton.Companion.addSectionButton
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

    override val root: Parent = vbox {
        addSectionButton(scope, viewModel)
        labeledSection(viewModel.arcSectionName) {
            id = viewModel.arcSectionId
            textfield(viewModel.arcSectionValue)
        }
    }

}