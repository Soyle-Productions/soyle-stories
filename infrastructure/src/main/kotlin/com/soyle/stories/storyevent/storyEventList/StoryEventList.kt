package com.soyle.stories.storyevent.storyEventList

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.makeEditable
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.createStoryEventDialog.createStoryEventDialog
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import tornadofx.*

class StoryEventList : View() {

	override val scope: ProjectScope = super.scope as ProjectScope

	private val model = resolve<StoryEventListModel>()

	private val viewListener = resolve<StoryEventListViewListener>()

	private lateinit var treeview: TreeView<StoryEventListItemViewModel?>

	override val root: Parent = stackpane {
		hgrow = Priority.SOMETIMES
		vgrow = Priority.ALWAYS
		emptyListDisplay(
		  model.hasStoryEvents,
		  model.emptyLabel,
		  model.createStoryEventButtonLabel
		) {
			createStoryEventDialog(scope)
		}
		vbox {
			visibleWhen { model.hasStoryEvents }
			managedProperty().bind(visibleProperty())
			minWidth = 200.0
			minHeight = 100.0
			vgrow = Priority.ALWAYS
			treeview<StoryEventListItemViewModel?>(TreeItem(null)) {
				this@StoryEventList.treeview = this
				isShowRoot = false
				vgrow = Priority.ALWAYS
				makeEditable { newName, oldValue ->
					// rename item
					if (oldValue != null) {
						viewListener.renameStoryEvent(oldValue.id, newName)
						//viewListener.renameScene(oldValue.id, newName)
					}

					oldValue
				}
				selectionModel.selectedItemProperty().onChange { model.selectedItem.value = it?.value }
				model.selectedItem.onChange { newSelection -> selectionModel.select(root.children.find { it.value?.id == newSelection?.id }) }
				model.selectedItem.onChange {
					contextMenu = when (it) {
						is StoryEventListItemViewModel -> storyEventContextMenu
						else -> null
					}
				}
				cellFormat {
					when (it) {
						is StoryEventListItemViewModel -> {
							graphic = label("${it.ordinal}.")
							text = it.name
						}
						else -> throw IllegalArgumentException("Invalid value type")
					}
				}
				populate { parentItem: TreeItem<StoryEventListItemViewModel?> ->
					when (parentItem.value) {
						null -> model.storyEvents
						else -> emptyList()
					}
				}
				onDoubleClick {
				}
			}
			hbox(alignment = Pos.CENTER, spacing = 10.0) {
				addClass("action-bar")
				isFillHeight = false
				padding = Insets(5.0, 0.0, 5.0, 0.0)
				button(model.createStoryEventButtonLabel) {
					addClass("create")
					isDisable = false
					action {
						createStoryEventDialog(scope)
					}
					isMnemonicParsing = false
				}
				button("Delete") {
					addClass("delete")
					enableWhen { model.selectedItem.isNotNull }
					action {
						when (val selectedItem = model.selectedItem.value) {
							//is StoryEventListItemViewModel -> deleteSceneDialog(scope, selectedItem)
						}
					}
					isMnemonicParsing = false
				}
			}
		}
	}

	private val storyEventContextMenu = ContextMenu().apply {
		item("Open") {
			id = "open"
			action {
				val selectedItem = model.selectedItem.value ?: return@action
				viewListener.openStoryEventDetails(selectedItem.id)
			}
		}
		item("Insert New Story Event Before") {
			id = "insert-before"
			action {
				val selectedItem = model.selectedItem.value ?: return@action
				createStoryEventDialog(scope, selectedItem.id, true)
			}
		}
		item("Insert New Story Event After") {
			id = "insert-after"
			action {
				val selectedItem = model.selectedItem.value ?: return@action
				createStoryEventDialog(scope, selectedItem.id, false)
			}
		}
		item("Rename") {
			id = "rename"
			action {
				treeview.edit(treeview.selectionModel.selectedItem)
			}
		}
		item("Delete") {
			id = "delete"
			action {
				when (val selectedItem = model.selectedItem.value) {
					//is StoryEventListItemViewModel -> deleteSceneDialog(scope, selectedItem)
				}
			}
		}
	}

	init {
		titleProperty.bind(model.toolTitle)

		viewListener.getValidState()
	}
}