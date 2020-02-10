package com.soyle.studio.characterarc.characterList

import javafx.scene.control.TreeItem
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:59 AM
 */
internal class PopulatedDisplay : View() {

    private val model by inject<CharacterListModel>()

    override val root = vbox {
        visibleWhen { model.hasCharacters }
        minWidth = 200.0
        treeview<Any?>(TreeItem(null)) {
            isShowRoot = false
            vgrow = Priority.ALWAYS
            bindSelected(model.selectedItem)
            setOnContextMenuRequested {
                contextMenu.hide()
                it.consume()
            }
            contextmenu {
                item("Compare Characters") {
                    action {

                    }
                }
            }
            val dataToItemMap = mutableMapOf<String, TreeItem<*>>()
            populate { parentItem: TreeItem<Any?> ->
                /*
                If the user has an item expanded, it should stay expanded.
                 */
                val parentItemValue = parentItem.value
                if (parentItemValue is CharacterItemViewModel) {
                    val oldItem = dataToItemMap[parentItemValue.id]
                    dataToItemMap[parentItemValue.id] = parentItem
                    if (oldItem != null) {
                        parentItem.isExpanded = oldItem.isExpanded
                    }
                }
                when (parentItemValue) {
                    null -> model.characters
                    is CharacterItemViewModel -> {
                        parentItemValue.arcs
                    }
                    else -> emptyList()
                }
            }
            // remove unused section id's from dataToItemMap
            root.children.onChange {
                while (it.next()) {
                    it.list
                        .filter { it.value is CharacterItemViewModel && (it.value as CharacterItemViewModel).id !in dataToItemMap }
                        .forEach {
                            dataToItemMap.remove((it.value as CharacterItemViewModel).id)
                        }
                }
            }
            onDoubleClick {

            }
        }
        this += find<ActionBar>()
    }
}
