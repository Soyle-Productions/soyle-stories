package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.makeEditable
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.createSceneDialog.createSceneDialog
import com.soyle.stories.scene.deleteSceneDialog.deleteSceneDialog
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import tornadofx.*

class SceneList : View() {

	override val scope: ProjectScope = super.scope as ProjectScope

	private val model = resolve<SceneListModel>()

	private val viewListener = resolve<SceneListViewListener>()

	private lateinit var treeview: TreeView<SceneItemViewModel?>

	override val root: Parent = stackpane {
		hgrow = Priority.SOMETIMES
		vgrow = Priority.ALWAYS
		emptyListDisplay(
		  model.hasScenes,
		  model.emptyLabel,
		  model.createSceneButtonLabel
		) {
			createSceneDialog(scope)
		}
		vbox {
			visibleWhen { model.hasScenes }
			managedProperty().bind(visibleProperty())
			minWidth = 200.0
			minHeight = 100.0
			vgrow = Priority.ALWAYS
			treeview<SceneItemViewModel?>(TreeItem(null)) {
				this@SceneList.treeview = this
				isShowRoot = false
				vgrow = Priority.ALWAYS
				makeEditable { newName, oldValue ->
					// rename item
					if (oldValue != null) {
						viewListener.renameScene(oldValue.id, newName)
					}

					oldValue
				}
				selectionModel.selectedItemProperty().onChange { model.selectedItem.value = it?.value }
				model.selectedItem.onChange { newSelection -> selectionModel.select(root.children.find { it.value?.id == newSelection?.id }) }
				model.selectedItem.onChange {
					contextMenu = when (it) {
						is SceneItemViewModel -> sceneContextMenu
						else -> null
					}
				}
				cellFormat {
					when (it) {
						is SceneItemViewModel -> {
							graphic = label("${(it.index+1)}.")
							text = it.name
						}
						else -> throw IllegalArgumentException("Invalid value type")
					}
				}
				populate { parentItem: TreeItem<SceneItemViewModel?> ->
					when (parentItem.value) {
						null -> model.scenes
						else -> emptyList()
					}
				}
				onDoubleClick {
				}
			}
			hbox(alignment = Pos.CENTER, spacing = 10.0) {
				isFillHeight = false
				padding = Insets(5.0, 0.0, 5.0, 0.0)
				button(model.createSceneButtonLabel) {
					id = "actionBar_create"
					isDisable = false
					action {
						createSceneDialog(scope)
					}
					isMnemonicParsing = false
				}
				button("Delete") {
					id = "actionBar_delete"
					enableWhen { model.selectedItem.isNotNull }
					action {
						when (val selectedItem = model.selectedItem.value) {
							is SceneItemViewModel -> deleteSceneDialog(scope, selectedItem)
						}
					}
					isMnemonicParsing = false
				}
			}
		}
	}

	private val sceneContextMenu = ContextMenu().apply {
		item("Rename") {
			id = "rename"
			action {
				treeview.edit(treeview.selectionModel.selectedItem)
			}
		}
		item("Insert New Scene Before") {
			action {
				val selectedItem = model.selectedItem.value?.id ?: return@action
				createSceneDialog(scope, selectedItem, true)
			}
		}
		item("Insert New Scene After") {
			action {
				val selectedItem = model.selectedItem.value?.id ?: return@action
				createSceneDialog(scope, selectedItem, false)
			}
		}
		item("Delete") {
			id = "delete"
			action {
				when (val selectedItem = model.selectedItem.value) {
					is SceneItemViewModel -> deleteSceneDialog(scope, selectedItem)
				}
			}
		}
	}

	init {
		titleProperty.bind(model.toolTitle)

		viewListener.getValidState()
	}

}