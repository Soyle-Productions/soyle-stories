package com.soyle.stories.character.list

import com.soyle.stories.characterarc.characterList.CharacterArcItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.*
import com.soyle.stories.common.components.cardBody
import com.soyle.stories.common.components.cardHeader
import com.soyle.stories.common.components.layouts.WaterfallPane
import com.soyle.stories.common.components.surfaces.*
import com.soyle.stories.common.components.surfaces.Surface.Companion.surface
import com.soyle.stories.common.components.text.ResizableLabel
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.di.get
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.Stylesheet.Companion.arrow
import tornadofx.Stylesheet.Companion.arrowButton
import tornadofx.Stylesheet.Companion.expanded

class CharacterItemGrid : Fragment() {

    private val viewModel = scope.get<CharacterListState>()

    private val characterContextMenu = ContextMenu().apply { item("") }

    override val root: Parent = surface<WaterfallPane>(elevation = Elevation.getValue(8), classes = {}) {
        hgap = 8.0
        vgap = 8.0
    //    rowValignment = VPos.TOP
        content()
    }

    @ViewBuilder
    private fun Pane.content() {/*
        scopedListener(viewModel.characterItems) { newItems ->
            when (newItems) {
                null -> {
                    children.clear()
                }
                else -> {
                    val newNodes = newItems.mapIndexed { index, treeItem ->
                        treeItem as TreeItem<CharacterListState.SelectableCharacterListItem>
                        val node = children.getOrNull(index) ?: characterCard(treeItem)
                        val characterItemProperty =
                            node.properties["characterItemProperty"] as SimpleObjectProperty<TreeItem<CharacterListState.SelectableCharacterListItem>>
                        characterItemProperty.value = treeItem
                        node
                    }
                    children.setAll(newNodes)
                }
            }
        }
    }

    private fun characterCard(initialItem: TreeItem<CharacterListState.SelectableCharacterListItem>) = Surface(VBox(), elevation = 10).apply {
        surfaceRelativeElevation = 2
        val characterListItemProperty = SimpleObjectProperty<TreeItem<CharacterListState.SelectableCharacterListItem>>(initialItem)
        properties["characterItemProperty"] = characterListItemProperty

        val selectableItemProperty = characterListItemProperty.select { it.valueProperty() }
        val characterItemProperty =
            selectableItemProperty.objectBinding { (it as? CharacterListState.SelectableCharacterItem)?.characterItem }

        val hasArcsProperty = characterListItemProperty.select { booleanBinding(it.children) { isNotEmpty() } }
        val expandedProperty = characterListItemProperty.select { it.expandedProperty() }

        addClass(CharacterListStyles.characterCard)
        toggleClass(expanded, expandedProperty)
        cardHeader {
            characterCardBehavior(characterItemProperty)
            add(characterIcon(characterItemProperty.stringBinding { it?.imageResource }))
            sectionTitle(characterItemProperty.stringBinding { it?.characterName })
            spacer()
            stackpane {
                existsWhen(hasArcsProperty)
                setOnMouseReleased { characterListItemProperty.value.isExpanded = !expandedProperty.value }
                addClass(arrowButton)
                stackpane {
                    addClass(arrow)
                }
            }
        }
        cardBody {
            isFillWidth = true
            existsWhen(hasArcsProperty.booleanBinding(expandedProperty) {
                (hasArcsProperty.value ?: false) && (expandedProperty.value ?: false)
            })
            val arcsProperty = SimpleListProperty<TreeItem<CharacterListState.SelectableCharacterListItem>>(
                observableListOf())
            var contentBinding: ObservableList<TreeItem<CharacterListState.SelectableCharacterListItem>>? = null
            scopedListener(characterListItemProperty) {
                contentBinding?.let(arcsProperty::unbindContent)
                contentBinding = (it?.children ?: observableListOf<TreeItem<CharacterListState.SelectableCharacterListItem>>())
                arcsProperty.bindContent(contentBinding)
            }
            dynamicContent(arcsProperty) {
                it?.forEach {
                    characterArcNode((it.value as CharacterListState.SelectableArcItem).arcItem)
                }
            }
        }
        maxHeight = Region.USE_PREF_SIZE
    }

    private fun Node.characterCardBehavior(characterItemProperty: ObservableValue<CharacterItemViewModel?>) {
        scopedListener(characterItemProperty) { userData = it }

        val selectableCharacterItem =
            characterItemProperty.objectBinding { it?.let(CharacterListState::SelectableCharacterItem) }

        scopedListener(selectableCharacterItem) {
            val selectableProperty = makeSelectable(focusTraversable = true)
            if (it != null) {
                selectableProperty.selectWhenViewModelSelectionIsSameItem(it)
            }
        }

        applyContextMenu(characterContextMenu) {
            val characterItem = selectableCharacterItem.value ?: return@applyContextMenu
            viewModel.selectedCharacterListItem.set(characterItem)
            characterContextMenu.items.setAll(characterOptions(scope, characterItem.characterItem))
        }
    }

    @ViewBuilder
    private fun Parent.characterArcNode(arcItem: CharacterArcItemViewModel) {
        val selectableArcItem = CharacterListState.SelectableArcItem(arcItem)

        surface(ResizableLabel(arcItem.name), elevation = 10) {
            addClass(CharacterListStyles.characterArc)

            // when hovered, lift the card by 2dp
            surfaceRelativeElevationProperty().bind(hoverProperty().integerBinding { if (it == true) 2 else 0 })
            style { borderWidth = multi(box(0.px)) }

            makeSelectable().selectWhenViewModelSelectionIsSameItem(selectableArcItem)

            // saves on node instantiation
            reuseCharacterContextMenu(selectableArcItem)
        }
    }

    private fun BooleanProperty.selectWhenViewModelSelectionIsSameItem(selectableListItem: CharacterListState.SelectableCharacterListItem)
    {
        // effectively bindSemi-Directional.  When the selectedCharacterListItem is the same as this arc, make sure
        // this node appears selected.  This is good for when the grid view is switched and the arc was previously
        // selected in the list view.
        softBind(this@CharacterItemGrid.viewModel.selectedCharacterListItem) {
            it?.isSameSelectableAs(selectableListItem)
        }
        // when this node is selected, should update the viewModel selectedCharacterListItem in case the view is
        // switched back to list view.  This will keep the arc selected in the other view.
        onChange { if (it) this@CharacterItemGrid.viewModel.selectedCharacterListItem.set(selectableListItem) }
    }

    private fun Node.reuseCharacterContextMenu(selectableArcItem: CharacterListState.SelectableArcItem)
    {
        applyContextMenu(characterContextMenu) { // onRequestContextMenu ->
            viewModel.selectedCharacterListItem.set(selectableArcItem)
            characterContextMenu.items.setAll(characterArcOptions(scope, selectableArcItem.arcItem))
        }
*/
    }


}