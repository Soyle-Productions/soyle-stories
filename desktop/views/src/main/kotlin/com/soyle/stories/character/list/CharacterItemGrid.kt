package com.soyle.stories.character.list

import com.soyle.stories.character.list.CharacterListView.Companion.isSameCharacterAs
import com.soyle.stories.characterarc.characterList.CharacterListItemViewModel
import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.card
import com.soyle.stories.common.components.cardHeader
import com.soyle.stories.common.components.surfaces.*
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.di.get
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.layout.FlowPane
import javafx.scene.layout.TilePane
import tornadofx.*
import java.lang.ref.WeakReference

class CharacterItemGrid : Fragment() {

    private val viewModel = scope.get<CharacterListState>()

    private val characterContextMenu = ContextMenu().apply { item("") }

    override val root: Parent = surface<TilePane>(elevation = 8) {
        hgap = 8.0
        vgap = 8.0
        children.bind(viewModel.characters) {
            characterCard(it)
        }
    }

    @ViewBuilder
    private fun Parent.characterCard(characterItem: CharacterListItemViewModel) = surface(10) {
        addClass(CharacterListStyles.characterCard)
        characterCardBehavior(characterItem)
        cardHeader {
            add(characterIcon(characterItem.item.imageResource.toProperty()))
            sectionTitle(characterItem.item.characterName)
        }
    }

    private fun Node.characterCardBehavior(characterItem: CharacterListItemViewModel) {
        userData = characterItem

        val selectedProperty = makeSelectable(focusTraversable = true)
        surfaceRelativeElevationProperty().bind(hoverProperty().integerBinding { if (it == true) 2 else 0 })
        selectedProperty.softBind(viewModel.selectedCharacterItem) { it.isSameCharacterAs(characterItem) }
        selectedProperty.onChange { if (it) viewModel.selectedCharacterItem.set(characterItem) }

        applyContextMenu(characterContextMenu) {
            viewModel.selectedCharacterItem.set(characterItem)
            characterContextMenu.items.setAll(characterOptions(scope, characterItem))
        }
    }
}