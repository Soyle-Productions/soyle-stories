package com.soyle.stories.scene

import com.soyle.stories.DependentProperty
import com.soyle.stories.ReadOnlyDependentProperty
import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.DeleteSceneDialogDriver.interact
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialog
import com.soyle.stories.scene.deleteSceneDialog.deleteSceneDialog
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.usecases.listAllScenes.SceneItem
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.CheckBox
import javafx.scene.control.DialogPane
import javafx.stage.Window
import kotlinx.coroutines.runBlocking
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent

object DeleteSceneDialogDriver : ApplicationTest() {

	val targetScene = object : DependentProperty<Scene> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps.Driver::givenProjectHasBeenOpened,
		  { double: SoyleStoriesTestDouble -> ScenesDriver.givenNumberOfCreatedScenesIsAtLeast(double, 1) } as (SoyleStoriesTestDouble) -> Unit
		)

		override fun get(double: SoyleStoriesTestDouble): Scene? {
			val scope = ProjectSteps.getProjectScope(double) ?: return null
			return runBlocking {
				scope.get<SceneRepository>().listAllScenesInProject(Project.Id(scope.projectId)).firstOrNull()
			}
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {}
	}

	val openWindow = object : DependentProperty<Window> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  targetScene::given
		)

		override fun get(double: SoyleStoriesTestDouble): Window? {
			ProjectSteps.getProjectScope(double) ?: return null
			return listWindows().find {
				it.scene.root.uiComponent<DeleteSceneDialog>() != null
			}?.takeIf { it.isShowing }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val scene = targetScene.get(double)!!
			interact {
				deleteSceneDialog(scope, SceneItemViewModel(SceneItem(scene.id.uuid, scene.name, 0)))
			}
		}
	}

	val openDialog = object : DependentProperty<DialogPane> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  openWindow::given
		)

		override fun get(double: SoyleStoriesTestDouble): DialogPane? {
			return openWindow.get(double)?.scene?.root as? DialogPane
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {}
	}

	fun isShowingNameOf(scene: SceneItemViewModel) = object : ReadOnlyDependentProperty<String> {
		override fun get(double: SoyleStoriesTestDouble): String? {
			val dialog = openDialog.get(double) ?: return null
			return dialog.headerText
		}

		override fun check(double: SoyleStoriesTestDouble): Boolean = get(double)?.contains(scene.name) ?: false
	}

	fun button(label: String) = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			val dialog = openDialog.get(double) ?: return null
			val buttonBar = from(dialog).lookup(".button-bar").queryAllAs(ButtonBar::class.java).firstOrNull()
			return buttonBar?.buttons?.find { it is Button && it.text == label } as? Button
		}
	}

	val doNotShowCheckbox = object: ReadOnlyDependentProperty<CheckBox> {
		override fun get(double: SoyleStoriesTestDouble): CheckBox? {
			val dialog = openDialog.get(double) ?: return null
			return from(dialog).lookup(".check-box").queryAllAs(CheckBox::class.java).firstOrNull()
		}
	}

}