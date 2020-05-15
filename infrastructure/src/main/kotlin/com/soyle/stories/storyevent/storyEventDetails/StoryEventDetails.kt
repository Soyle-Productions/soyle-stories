package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.StoryEventDetailsToolViewModel
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.*

class StoryEventDetails : View() {

	override val scope = super.scope as StoryEventDetailsScope

	override val root: Parent = form {
		fieldset {
			field {
				button {
					id = "location-select"
					isDisable = true
				}
			}
		}
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