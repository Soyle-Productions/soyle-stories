package com.soyle.stories.theme.characterValueComparison

import com.soyle.stories.characterarc.Styles.Companion.defaultCharacterImage
import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.components.*
import com.soyle.stories.common.components.ComponentsStyles.Companion.notFirstChild
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.characterValueComparison.components.CharacterCard
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.Parent
import javafx.scene.control.ContentDisplay
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.util.Duration
import tornadofx.*

class CharacterValueComparison : View() {

    override val scope: CharacterValueComparisonScope = super.scope as CharacterValueComparisonScope

    private val viewListener = resolve<CharacterValueComparisonViewListener>()
    private val model = resolve<CharacterValueComparisonModel>()

    override val root: Parent = vbox {
        hbox {
            spacing = 8.0
            padding = Insets(8.0)
            button(model.openValueWebToolButtonLabel) {
                graphic = MaterialIconView(MaterialIcon.CALL_MADE)
                contentDisplay = ContentDisplay.RIGHT
                action { viewListener.openValueWebTool(scope.type.themeId.toString()) }
            }
            spacer()
            buttonCombo {
                textProperty().bind(model.addCharacterButtonLabel)
                val loadingItem = item("Loading...") {
                    isDisable = true
                }
                val createCharacterItem = MenuItem("[Create New Character]").apply {
                    action {
                        createCharacterDialog(scope.projectScope, scope.type.themeId.toString())
                    }
                }
                model.availableCharacters.onChange {
                    items.clear()
                    when {
                        it == null -> items.add(loadingItem)
                        it.isEmpty() -> {
                            items.add(createCharacterItem)
                            item("No available characters") { isDisable = true }
                        }
                        else -> {
                            items.add(createCharacterItem)
                            it.forEach {
                                item(it.characterName) {
                                    action { viewListener.addCharacter(it.characterId) }
                                }
                            }
                        }
                    }
                }
                setOnShowing {
                    viewListener.getAvailableCharacters()
                }
                setOnHidden {
                    model.availableCharacters.value = null
                }
            }
        }
        flowpane {
            hgap = 8.0
            vgap = 8.0
            padding = Insets(8.0)
            rowValignment = VPos.TOP
            id = "character-values"
            model.characters.forEachIndexed { i, it ->
                this += resolve<CharacterCard>().apply {
                    itemProperty.bind(model.characters.select { it.getOrNull(i).toProperty() })
                }
            }
            model.characters.addListener { observable, oldValue, newValue ->
                val oldSize = oldValue?.size ?: 0
                val newSize = newValue?.size ?: 0
                if (newSize > oldSize) {
                    repeat(newSize - oldSize) { i ->
                        this += resolve<CharacterCard>().apply {
                            itemProperty.bind(model.characters.select { it.getOrNull(i + oldSize).toProperty() })
                        }
                    }
                }
            }
        }
    }

    init {
        viewListener.getValidState()
    }

}