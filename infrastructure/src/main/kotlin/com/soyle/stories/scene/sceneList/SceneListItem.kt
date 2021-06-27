package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.editingCell
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.get
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialog
import com.soyle.stories.scene.sceneList.SceneListItem.Styles.Companion.hasIssue
import com.soyle.stories.scene.sceneList.SceneListItem.Styles.Companion.sceneListCell
import com.soyle.stories.soylestories.Styles.Companion.Orange
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import tornadofx.*

class SceneListItem : TreeCellFragment<SceneItemViewModel?>() {

    private val sceneListState
        get() = scope.get<SceneListModel>()

    private val viewListener
        get() = scope.get<SceneListViewListener>()

    private val isEditing = sceneListState.editingItem.booleanBinding(itemProperty) {
        it?.id == item?.id
    }

    private val sceneName = itemProperty.stringBinding {
        it?.name
    }
    private val sceneText = itemProperty.stringBinding {
        "${item?.index?.plus(1)}. ${sceneName.value}"
    }

    private val hasProblem = itemProperty.booleanBinding {
        it?.hasProblem ?: false
    }

    override val root: Parent = hbox {
        dynamicContent(isEditing) {
            if (it == true) textfield(sceneName.value) {
                useMaxWidth = true
                selectAll()
                requestFocus()
                action {
                    decorators.clear()
                    renameScene(text)?.let {
                        addDecorator(SimpleMessageDecorator(it.localizedMessage, ValidationSeverity.Error))
                    }
                }
                setOnKeyPressed {
                    if (it.code == KeyCode.ESCAPE) { stopEdit() }
                }
                onLoseFocus { stopEdit() }
            }
            else label(sceneText) {
                toggleClass(hasIssue, hasProblem)
            }
        }
    }

    init {
        cellProperty.onChange {
            it?.addClass(sceneListCell)
            it?.enableDrag()
            it?.enableDrop { sceneId, name, newIndex ->
                scope.get<ReorderSceneDialog>().show(sceneId, name, newIndex)
            }
        }
        isEditing.onChange {
            val cell = cell ?: return@onChange
            val treeView = cell.treeView ?: return@onChange
            if (! it && treeView.editingCell == cell) {
                treeView.properties.put("com.soyle.stories.treeView.editingCell", null)
            } else if (it) {
                treeView.properties.put("com.soyle.stories.treeView.editingCell", cell)
            }
        }
    }

    private fun stopEdit() {
        sceneListState.editingItem.value = null
    }

    private fun renameScene(newName: String): Error?
    {
        val sceneId = item?.id ?: return null
        val validName = NonBlankString.create(newName) ?: return Error("Name cannot be blank")
        stopEdit()
        viewListener.renameScene(sceneId, validName)
        return null
    }

    class Styles : Stylesheet() {

        companion object {

            val sceneListCell by cssclass()

            val hasIssue by csspseudoclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            sceneListCell {
                padding = box(4.px, 0.px)
                borderInsets = multi(box((-4).px, 0.px, 0.px, 0.px))
                borderWidth = multi(box(4.px))
            }
            hasIssue {
                borderWidth += box(0.px, 0.px, 2.px, 0.px)
                borderColor += box(Color.TRANSPARENT, Color.TRANSPARENT, Orange, Color.TRANSPARENT)
            }
        }

    }

}