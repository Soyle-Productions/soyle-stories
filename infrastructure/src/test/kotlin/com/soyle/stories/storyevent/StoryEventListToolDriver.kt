package com.soyle.stories.storyevent

import com.soyle.stories.DependentProperty
import com.soyle.stories.ReadOnlyDependentProperty
import com.soyle.stories.common.editingCell
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.storyevent.StoryEventListToolDriver.interact
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.storyEventList.StoryEventList
import com.soyle.stories.testutils.findComponentsInScope
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.select
import tornadofx.selectFirst

object StoryEventListToolDriver : ApplicationTest() {

	val openTool = object : DependentProperty<StoryEventList> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps::givenProjectHasBeenOpened
		)

		override fun get(double: SoyleStoriesTestDouble): StoryEventList? {
			val scope = ProjectSteps.getProjectScope(double) ?: return null
			return findComponentsInScope<StoryEventList>(scope).firstOrNull()
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)
			ProjectSteps.whenMenuItemIsSelected(double, "tools", "tools_Story Events")
		}
	}

	val tabSelected = object : DependentProperty<StoryEventList> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  openTool::given
		)

		override fun get(double: SoyleStoriesTestDouble): StoryEventList? {
			val list = openTool.get(double) ?: return null
			return list.takeIf { it.owningTab?.isSelected == true }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val list = openTool.get(double)!!
			list.owningTab!!.select()
		}
	}

	private val emptyDisplay = object : ReadOnlyDependentProperty<Node> {
		override fun get(double: SoyleStoriesTestDouble): Node? {
			val list = openTool.get(double) ?: return null
			return from(list.root).lookup(".empty-display").queryAll<Node>().firstOrNull()
		}
	}

	private val itemList = object : DependentProperty<TreeView<StoryEventListItemViewModel?>> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  tabSelected::given
		)
		override fun get(double: SoyleStoriesTestDouble): TreeView<StoryEventListItemViewModel?>? {
			val list = tabSelected.get(double) ?: return null
			return from(list.root).lookup(".tree-view").queryAll<TreeView<StoryEventListItemViewModel?>>().firstOrNull()
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			// no-op
		}
	}

	val listedItems = object : ReadOnlyDependentProperty<List<TreeItem<StoryEventListItemViewModel?>>> {
		override fun get(double: SoyleStoriesTestDouble): List<TreeItem<StoryEventListItemViewModel?>>? {
			return itemList.get(double)?.root?.children ?: emptyList()
		}
	}

	val visibleEmptyDisplay = object : ReadOnlyDependentProperty<Node> {
		override fun get(double: SoyleStoriesTestDouble): Node? {
			return emptyDisplay.get(double)?.takeIf { it.visibleProperty().get() }
		}
	}

	val centerButton = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			val emptyPane = emptyDisplay.get(double) ?: return null
			return from(emptyPane).lookup(".center-button").queryAll<Button>().firstOrNull()
		}
	}

	fun actionBarButton(buttonClass: String) = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			val list = openTool.get(double) ?: return null
			return from(list.root).lookup(".action-bar").lookup(".$buttonClass").queryAll<Button>().firstOrNull()
		}
	}

	fun isShowingStoryEvent(storyEvent: StoryEvent) = object : ReadOnlyDependentProperty<StoryEventListItemViewModel> {
		override fun get(double: SoyleStoriesTestDouble): StoryEventListItemViewModel? {
			val treeView = itemList.get(double) ?: return null
			return treeView.root.children.asSequence().mapNotNull { it.value }.find {
				it.id == storyEvent.id.uuid.toString()
			}
		}
	}

	fun isShowingStoryEventAtEnd(storyEvent: StoryEvent) = object : ReadOnlyDependentProperty<StoryEventListItemViewModel> {
		override fun get(double: SoyleStoriesTestDouble): StoryEventListItemViewModel? {
			val treeView = itemList.get(double) ?: return null
			val lastItem = treeView.root.children.asSequence().mapNotNull { it.value }.lastOrNull()
			return lastItem.takeIf { it?.id == storyEvent.id.uuid.toString() }
		}
	}

	fun isShowingStoryEventBefore(storyEvent: StoryEvent, relativeId: StoryEvent.Id) = object : ReadOnlyDependentProperty<StoryEventListItemViewModel> {
		override fun get(double: SoyleStoriesTestDouble): StoryEventListItemViewModel? {
			val treeView = itemList.get(double) ?: return null
			val items = treeView.root.children.mapNotNull { it.value }
			val itemIndex = items.indexOfFirst { it.id == storyEvent.id.uuid.toString() }
			return items.getOrNull(itemIndex + 1)?.takeIf { it.id == relativeId.uuid.toString() }
		}
	}

	fun isShowingStoryEventAfter(storyEvent: StoryEvent, relativeId: StoryEvent.Id) = object : ReadOnlyDependentProperty<StoryEventListItemViewModel> {
		override fun get(double: SoyleStoriesTestDouble): StoryEventListItemViewModel? {
			val treeView = itemList.get(double) ?: return null
			val items = treeView.root.children.mapNotNull { it.value }
			val itemIndex = items.indexOfFirst { it.id == storyEvent.id.uuid.toString() }
			return items.getOrNull(itemIndex - 1)?.takeIf { it.id == relativeId.uuid.toString() }
		}
	}

	val selectedItem = object : DependentProperty<TreeItem<StoryEventListItemViewModel?>> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  StoryEventsDriver.storyEventCreated()::given,
		  itemList::given
		)

		override fun get(double: SoyleStoriesTestDouble): TreeItem<StoryEventListItemViewModel?>? {
			val list = itemList.get(double) ?: return null
			return list.selectionModel.selectedItem
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val list = itemList.get(double)!!
			interact {
				list.selectFirst()
			}
		}
	}

	val openRightClickMenu = object : DependentProperty<ContextMenu> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  selectedItem::given
		)

		override fun get(double: SoyleStoriesTestDouble): ContextMenu? {
			val list = itemList.get(double) ?: return null
			var menu: ContextMenu? = null
			interact {
				menu = list.contextMenu?.takeIf { it.isShowing }
			}
			return menu
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val list = itemList.get(double)!!
			interact {
				list.contextMenu.show(list, Side.TOP, 0.0, 0.0)
			}
		}
	}

	fun rightClickMenuOption(option: String) = object : DependentProperty<MenuItem> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  openRightClickMenu::given
		)

		override fun get(double: SoyleStoriesTestDouble): MenuItem? {
			val contextMenu = openRightClickMenu.get(double) ?: return null
			return contextMenu.items.find { it.text == option }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val contextMenu = openRightClickMenu.get(double)!!
			interact {
				contextMenu.items.find { it.text == option }!!.fire()
			}
		}
	}

	val visibleRenameInputBox = object : DependentProperty<TextField>
	{
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  selectedItem::given
		)

		override fun get(double: SoyleStoriesTestDouble): TextField? {
			val list = itemList.get(double) ?: return null
			return (list.editingCell?.graphic as? TextField)?.takeIf { it.isVisible }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val list = itemList.get(double)!!
			interact {
				list.edit(selectedItem.get(double)!!)
			}
		}
	}

	val renameText = object : ReadOnlyDependentProperty<String>
	{
		override fun get(double: SoyleStoriesTestDouble): String? {
			val inputBox = visibleRenameInputBox.get(double) ?: return null
			return inputBox.text
		}
	}

	val validStoryEventNameInRenameBox = object : DependentProperty<String>
	{
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  visibleRenameInputBox::given
		)

		override fun get(double: SoyleStoriesTestDouble): String? {
			return renameText.get(double)?.takeIf { it == "Valid Story Event Name" }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val inputBox = visibleRenameInputBox.get(double)!!
			interact {
				inputBox.text = "Valid Story Event Name"
			}
		}
	}

}