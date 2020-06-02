package com.soyle.stories.scene

import com.soyle.stories.DependentProperty
import com.soyle.stories.ReadOnlyDependentProperty
import com.soyle.stories.di.get
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.DeleteSceneRamificationsDriver.interact
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamifications
import com.soyle.stories.scene.deleteSceneRamifications.RamificationsScope
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import javafx.scene.Node
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.select

object DeleteSceneRamificationsDriver : ApplicationTest() {

	val tool = object : DependentProperty<DeleteSceneRamifications> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps::givenProjectHasBeenOpened,
		  { it: SoyleStoriesTestDouble -> ScenesDriver.givenNumberOfCreatedScenesIsAtLeast(it, 1) } as (SoyleStoriesTestDouble) -> Unit
		)

		override fun get(double: SoyleStoriesTestDouble): DeleteSceneRamifications? {
			val projectScope = ProjectSteps.getProjectScope(double) ?: return null
			val scope = projectScope.toolScopes.asSequence().filterIsInstance<RamificationsScope>().firstOrNull() ?: return null
			return scope.get()
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val projectScope = ProjectSteps.getProjectScope(double) ?: return
			interact {
				projectScope.get<OpenToolController>().openDeleteSceneRamificationsTool(
				  ScenesDriver.getCreatedScenes(double).first().id.uuid.toString()
				)
			}
		}

	}

	val openTool = object : DependentProperty<DeleteSceneRamifications> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = tool.dependencies

		override fun get(double: SoyleStoriesTestDouble): DeleteSceneRamifications? {
			return tool.get(double)?.takeIf { it.root.visibleProperty().get() }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			tool.whenSet(double)
			val ramifications = tool.get(double)!!
			ramifications.owningTab?.select()
		}
	}

	val okDisplay = object : ReadOnlyDependentProperty<Node> {
		override fun get(double: SoyleStoriesTestDouble): Node? {
			val root = tool.get(double)?.root
			return from(root).lookup(".ok").queryAll<Node>().firstOrNull()
		}

		override fun check(double: SoyleStoriesTestDouble): Boolean {
			return get(double)?.visibleProperty()?.get() == true
		}
	}

}