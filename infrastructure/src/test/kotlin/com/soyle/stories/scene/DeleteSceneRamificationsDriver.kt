package com.soyle.stories.scene

import com.soyle.stories.DependentProperty
import com.soyle.stories.ReadOnlyDependentProperty
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Scene
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.DeleteSceneRamificationsDriver.interact
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamifications
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsScope
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Labeled
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.select

object DeleteSceneRamificationsDriver : ApplicationTest() {

	fun tool(sceneId: Scene.Id) = object : DependentProperty<DeleteSceneRamifications> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps::givenProjectHasBeenOpened,
		  { it: SoyleStoriesTestDouble -> ScenesDriver.givenNumberOfCreatedScenesIsAtLeast(it, 1) } as (SoyleStoriesTestDouble) -> Unit
		)

		override fun get(double: SoyleStoriesTestDouble): DeleteSceneRamifications? {
			val projectScope = ProjectSteps.getProjectScope(double) ?: return null
			val scope = projectScope.toolScopes.asSequence().filterIsInstance<DeleteSceneRamificationsScope>().find {
				it.sceneId == sceneId.uuid.toString()
			} ?: return null
			return scope.get()
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val projectScope = ProjectSteps.getProjectScope(double) ?: return
			interact {
				projectScope.get<OpenToolController>().openDeleteSceneRamificationsTool(
				  sceneId.uuid.toString()
				)
			}
		}

	}

	fun openTool(sceneId: Scene.Id) = object : DependentProperty<DeleteSceneRamifications> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = tool(sceneId).dependencies

		override fun get(double: SoyleStoriesTestDouble): DeleteSceneRamifications? {
			return tool(sceneId).get(double)?.takeIf { it.root.visibleProperty().get() }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			tool(sceneId).whenSet(double)
			val ramifications = tool(sceneId).get(double)!!
			ramifications.owningTab?.select()
		}
	}

	fun okDisplay(sceneId: Scene.Id) = object : ReadOnlyDependentProperty<Node> {
		override fun get(double: SoyleStoriesTestDouble): Node? {
			val root = tool(sceneId).get(double)?.root
			return from(root).lookup(".ok").queryAll<Node>().firstOrNull()
		}

		override fun check(double: SoyleStoriesTestDouble): Boolean {
			return get(double)?.visibleProperty()?.get() == true
		}
	}

	fun listedScenes(sceneId: Scene.Id) = object : ReadOnlyDependentProperty<List<Parent>> {
		override fun get(double: SoyleStoriesTestDouble): List<Parent>? {
			val root = tool(sceneId).get(double)?.root ?: return null
			return from(root).lookup(".scene-item").queryAll<Parent>().toList()
		}

		override fun check(double: SoyleStoriesTestDouble): Boolean = ! get(double).isNullOrEmpty()
	}

	fun listedScene(focusSceneId: Scene.Id, targetSceneId: Scene.Id) = object : ReadOnlyDependentProperty<Parent> {
		override fun get(double: SoyleStoriesTestDouble): Parent? {
			val sceneItems = listedScenes(focusSceneId).get(double)
			return sceneItems?.find { it.id == targetSceneId.uuid.toString() }
		}
	}

	fun listedCharacter(focusSceneId: Scene.Id, targetSceneId: Scene.Id, characterId: Character.Id) = object : ReadOnlyDependentProperty<Node> {
		override fun get(double: SoyleStoriesTestDouble): Node? {
			val sceneItems = listedScenes(focusSceneId).get(double) ?: return null
			val targetSceneItem = sceneItems.find {
				it.id == targetSceneId.uuid.toString()
			} ?: return null
			return from(targetSceneItem).lookup(".character-item").queryAll<Node>().find {
				it.id == characterId.uuid.toString()
			}
		}
	}

	fun listedCharacter(focusSceneId: Scene.Id, characterId: Character.Id) = object : ReadOnlyDependentProperty<List<Node>> {
		override fun get(double: SoyleStoriesTestDouble): List<Node> {
			val sceneItems = listedScenes(focusSceneId).get(double) ?: return listOf()
			return sceneItems.asSequence().mapNotNull {
				from(it).lookup(".character-item").queryAll<Node>().find {
					it.id == characterId.uuid.toString()
				}
			}.toList()
		}

		override fun check(double: SoyleStoriesTestDouble): Boolean = get(double).isNotEmpty()
	}

	fun currentMotivation(focusSceneId: Scene.Id, targetSceneId: Scene.Id, characterId: Character.Id) = object : ReadOnlyDependentProperty<String> {
		override fun get(double: SoyleStoriesTestDouble): String? {
			val characterNode = listedCharacter(focusSceneId, targetSceneId, characterId).get(double) ?: return null
			return from(characterNode).lookup(".current").queryAll<Labeled>().firstOrNull()?.text
		}
	}

	fun changedMotivation(focusSceneId: Scene.Id, targetSceneId: Scene.Id, characterId: Character.Id) = object : ReadOnlyDependentProperty<String> {
		override fun get(double: SoyleStoriesTestDouble): String? {
			val characterNode = listedCharacter(focusSceneId, targetSceneId, characterId).get(double) ?: return null
			return from(characterNode).lookup(".changed").queryAll<Labeled>().firstOrNull()?.text
		}
	}

}