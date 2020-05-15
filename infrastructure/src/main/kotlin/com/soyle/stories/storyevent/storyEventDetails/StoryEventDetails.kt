package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.StoryEventDetailsToolViewModel
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.*

class StoryEventDetails : View() {

	override val scope = super.scope as StoryEventDetailsScope

	private val viewListener = resolve<StoryEventDetailsViewListener>()
	private val model = resolve<StoryEventDetailsModel>()

	private val locationSelectionList = ContextMenu().apply {
		isAutoHide = true
		isAutoFix = true
		items.bind(model.locations) {
			checkmenuitem(it.name) {
				isSelected = false // it.id == model.linkedLocationId
			}
		}
	}

	override val root: Parent = form {
		fieldset {
			field {
				button(model.locationSelectionButtonLabel) {
					id = "location-select"
					enableWhen { model.hasLocations }
					contextMenu = locationSelectionList
					setOnAction {
						it.consume()
						contextMenu.show(this, Side.BOTTOM, 0.0, 0.0)
					}
				}
			}
		}
	}

	init {
		titleProperty.bind(model.title)
		viewListener.getValidState()
	}

}

fun TabPane.storyEventDetailsTab(projectScope: ProjectScope, storyEventDetailsToolViewModel: StoryEventDetailsToolViewModel): Tab {
	val scope = StoryEventDetailsScope(projectScope, storyEventDetailsToolViewModel)
	val structure = find<StoryEventDetails>(scope = scope)
	val tab = tab(structure)
	tab.tabPaneProperty().onChange {
		if (it == null) {
			scope.close()
		}
	}
	return tab
}