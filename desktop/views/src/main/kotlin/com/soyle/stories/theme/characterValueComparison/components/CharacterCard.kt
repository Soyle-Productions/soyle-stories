package com.soyle.stories.theme.characterValueComparison.components

import com.soyle.stories.characterarc.CharacterArcStyles
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.common.components.*
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Companion.chip
import com.soyle.stories.common.components.ComponentsStyles.Companion.contextMenuSectionHeaderItem
import com.soyle.stories.common.components.ComponentsStyles.Companion.contextMenuSectionedItem
import com.soyle.stories.common.components.ComponentsStyles.Companion.discouragedSelection
import com.soyle.stories.common.components.ComponentsStyles.Companion.noDisableStyle
import com.soyle.stories.common.components.ComponentsStyles.Companion.noSelectionMenuItem
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.characterValueComparison.CharacterComparedWithValuesViewModel
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparisonModel
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparisonViewListener
import com.soyle.stories.theme.characterValueComparison.CharacterValueViewModel
import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButton
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialog
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.event.Event
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.scene.control.Tooltip
import javafx.scene.layout.Region
import javafx.util.Duration
import tornadofx.*
import java.util.*

class CharacterCard : ItemFragment<CharacterComparedWithValuesViewModel>() {

    private val viewListener by resolveLater<CharacterValueComparisonViewListener>()
    private val model by resolveLater<CharacterValueComparisonModel>()
    private val makeAddValueButton = resolve<AddValueButton.Factory>()

    private val viewModel = ItemViewModel<CharacterComparedWithValuesViewModel>()

    init {
        viewModel.itemProperty.bind(itemProperty)
    }

    private val nameProperty = viewModel.bind(CharacterComparedWithValuesViewModel::characterName)
    private val archetypeProperty = viewModel.bind { viewModel.item?.archetype?.label.toProperty() }
    private val isArchetypeSetProperty = viewModel.bind { viewModel.item?.archetype?.isEmptyValue.toProperty() }

    private val valueSectionHeaderLabelProperty =
        viewModel.bind(CharacterComparedWithValuesViewModel::valueSectionHeaderLabel)

    private val removeButtonLabelProperty = viewModel.bind(CharacterComparedWithValuesViewModel::removeButtonLabel)
    private val removeButtonToolTipProperty = viewModel.bind(CharacterComparedWithValuesViewModel::removeButtonToolTip)

    private val addValueButtonLabelProperty = viewModel.bind(CharacterComparedWithValuesViewModel::addValueButtonLabel)
    private val valuesProperty = viewModel.bindImmutableList(CharacterComparedWithValuesViewModel::values)

    override val root: Parent = card {
        idProperty().bind(itemProperty.stringBinding { it?.characterId })
        cardHeader {
            this += MaterialIconView(CharacterArcStyles.defaultCharacterImage, "2em")
            vbox {
                label(nameProperty) {
                    style { fontSize = 1.25.em }
                }
                spacer()
                editableText {
                    graphic = MaterialIconView(MaterialIcon.EDIT)
                    isArchetypeSetProperty.onChange {
                        if (it == null || it == true) {
                            textProperty.unbind()
                            placeholderTextProperty.cleanBind(archetypeProperty)
                        } else {
                            placeholderTextProperty.unbind()
                            textProperty.cleanBind(archetypeProperty)
                        }
                    }
                    setOnAction { _ ->
                        val characterId = itemProperty.get()?.characterId ?: return@setOnAction
                        viewListener.setCharacterArchetype(characterId, editedText ?: "")
                        hide()
                    }
                }
            }
            spacer()
            button(removeButtonLabelProperty) {
                tooltip {
                    textProperty().bind(removeButtonToolTipProperty)
                    style {
                        fontSize = 1.em
                    }
                    showDelay = Duration.seconds(0.0)
                    hideDelay = Duration.seconds(0.0)
                    this@button.focusedProperty().onChange {
                        if (it && !isShowing) {
                            val buttonBoundsOnScreen = this@button.localToScreen(this@button.boundsInLocal)
                            show(this@button, buttonBoundsOnScreen.minX, buttonBoundsOnScreen.maxY)
                        } else hide()
                    }
                }
                action {
                    val characterId = itemProperty.get()?.characterId ?: return@action
                    viewListener.removeCharacter(characterId)
                }
            }
        }
        cardBody {
            addClass(ComponentsStyles.notFirstChild)
            hbox {
                label(valueSectionHeaderLabelProperty) {
                    style { fontSize = 1.2.em }
                }
                spacer()
                val characterIdProperty = itemProperty.stringBinding { it?.characterId }
                val addValueButtonProperty = characterIdProperty.objectBinding {
                    if (it != null) {
                        makeAddValueButton(
                            Theme.Id(model.scope.type.themeId),
                            Character.Id(UUID.fromString(it))
                        )
                    } else null
                }
                addValueButtonProperty.addListener { observable, oldValue, newValue ->
                    if (oldValue != null && newValue != null) oldValue.replaceWith(newValue)
                    else {
                        oldValue?.removeFromParent()
                        newValue?.let { add(it) }
                    }
                }
            }
            vbox {
                isFillWidth = false
                bindChildren(valuesProperty) {
                    chip(it.label, onDelete = removeChip(it)) {
                        id = it.oppositionId
                        maxWidth = Region.USE_PREF_SIZE
                    }.node
                }
            }
        }
    }

    init {
        itemProperty.onChange {
            if (it == null) {
                viewModel.itemProperty.unbind()
                removeFromParent()
            }
        }
    }

    private fun removeChip(value: CharacterValueViewModel) = fun (_: Event) {
        val characterId = item?.characterId ?: return
        viewListener.removeOppositionValueFromCharacter(characterId, value.oppositionId)
    }

}