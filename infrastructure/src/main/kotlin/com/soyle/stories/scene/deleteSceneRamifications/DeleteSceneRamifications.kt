package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.di.get
import com.soyle.stories.layout.tools.temporary.Ramifications
import com.soyle.stories.project.ProjectScope
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.*

class DeleteSceneRamifications : View() {

	override val scope: RamificationsScope = super.scope as RamificationsScope

	override val root: Parent = pane {
		pane {
			addClass("ok")
			label("All good!")
		}
	}

}

fun TabPane.deleteSceneRamificationsTab(projectScope: ProjectScope, tool: Ramifications.DeleteSceneRamifications): Tab
{
	val scope = RamificationsScope(projectScope, tool)
	val view = scope.get<DeleteSceneRamifications>()
	val tab = tab(view)
	tab.tabPaneProperty().onChange {
		if (it == null) {
			scope.close()
		}
	}
	return tab
}