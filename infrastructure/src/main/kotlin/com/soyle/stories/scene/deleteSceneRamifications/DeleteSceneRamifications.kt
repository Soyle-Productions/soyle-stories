package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.layout.tools.temporary.Ramifications
import com.soyle.stories.project.ProjectScope
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.*

class DeleteSceneRamifications : View() {

	override val scope: DeleteSceneRamificationsScope = super.scope as DeleteSceneRamificationsScope
	private val viewListener = resolve<DeleteSceneRamificationsViewListener>()
	private val model = resolve<DeleteSceneRamificationsModel>()

	override val root: Parent = stackpane {
		pane {
			addClass("ok")
			label("All good!")
		}
		vbox {
			bindChildren(model.scenes) {
				sceneItem(it)
			}
		}
	}

	init {
		viewListener.getValidState()
	}
}

fun TabPane.deleteSceneRamificationsTab(projectScope: ProjectScope, tool: Ramifications.DeleteSceneRamifications): Tab
{
	val scope = DeleteSceneRamificationsScope(tool, projectScope)
	val view = scope.get<DeleteSceneRamifications>()
	val tab = tab(view)
	tab.tabPaneProperty().onChange {
		if (it == null) {
			scope.close()
		}
	}
	return tab
}