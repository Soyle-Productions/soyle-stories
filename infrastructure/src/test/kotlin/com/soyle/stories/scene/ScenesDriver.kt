package com.soyle.stories.scene

import com.soyle.stories.DependentProperty
import com.soyle.stories.UATLogger
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.ScenesDriver.interact
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

object ScenesDriver : ApplicationTest() {

	fun registerIdentifiers(double: SoyleStoriesTestDouble, identifiers: List<Pair<String, Scene.Id>>)
	{
		ProjectSteps.getProjectScope(double)?.get<WorkBench>()?.properties?.put("sceneIdFor", identifiers.toMap())
	}

	fun getSceneIdentifiers(double: SoyleStoriesTestDouble): Map<String, Scene.Id>?
	{
		return ProjectSteps.getProjectScope(double)?.get<WorkBench>()?.properties?.get("sceneIdFor") as? Map<String, Scene.Id>
	}

	fun getSceneIdByIdentifier(double: SoyleStoriesTestDouble, identifier: String): Scene.Id?
	{
		return getSceneIdentifiers(double)?.get(identifier)
	}

	fun getSceneByIdentifier(double: SoyleStoriesTestDouble, identifier: String): Scene?
	{
		val id = getSceneIdByIdentifier(double, identifier) ?: return null
		val repo = ProjectSteps.getProjectScope(double)?.get<SceneRepository>() ?: return null
		return runBlocking {
			repo.getSceneById(id)
		}
	}

	fun setNumberOfCreatedScenes(double: SoyleStoriesTestDouble, count: Int) {
		ProjectSteps.givenProjectHasBeenOpened(double)
		val createdCount = getNumberOfCreatedScenes(double)
		repeat(count - createdCount) {
			whenSceneIsCreated(double)
		}
	}

	fun getCreatedScenes(double: SoyleStoriesTestDouble): List<Scene> {
		val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		val repo = scope.get<SceneRepository>()
		return runBlocking {
			val order = repo.getSceneIdsInOrder(Project.Id(scope.projectId)).withIndex().associate { it.value to it.index }
			repo.listAllScenesInProject(Project.Id(scope.projectId)).sortedBy {
				order.getValue(it.id)
			}
		}
	}

	fun getNumberOfCreatedScenes(double: SoyleStoriesTestDouble): Int =
	  getCreatedScenes(double).size

	fun whenSceneIsCreated(double: SoyleStoriesTestDouble) {
		val scope = ProjectSteps.getProjectScope(double)!!
		val controller = scope.get<CreateNewSceneController>()
		controller.createNewScene("Unique Scene Name ${UUID.randomUUID()}")
	}

	fun givenNumberOfCreatedScenesIsAtLeast(double: SoyleStoriesTestDouble, count: Int) {
		UATLogger.log("Given Number of Created Scenes is At Least $count")
		if (getNumberOfCreatedScenes(double) < count) {
			setNumberOfCreatedScenes(double, count)
		}
		assertTrue(getNumberOfCreatedScenes(double) >= count)
	}

	fun createdSceneBefore(sceneId: Scene.Id) = object : DependentProperty<Scene> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  { double: SoyleStoriesTestDouble -> givenNumberOfCreatedScenesIsAtLeast(double, 1) } as (SoyleStoriesTestDouble) -> Unit
		)

		override fun get(double: SoyleStoriesTestDouble): Scene? {
			return null
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val controller = scope.get<CreateNewSceneController>()
			interact {
				controller.createNewSceneBefore("Unique Scene Name ${UUID.randomUUID()}", sceneId.uuid.toString())
			}
		}
	}

	fun createdSceneAfter(sceneId: Scene.Id) = object : DependentProperty<Scene> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  { double: SoyleStoriesTestDouble -> givenNumberOfCreatedScenesIsAtLeast(double, 1) } as (SoyleStoriesTestDouble) -> Unit
		)

		override fun get(double: SoyleStoriesTestDouble): Scene? {
			return null
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val controller = scope.get<CreateNewSceneController>()
			interact {
				controller.createNewSceneAfter("Unique Scene Name ${UUID.randomUUID()}", sceneId.uuid.toString())
			}
		}
	}

	fun whenSceneIsDeleted(double: SoyleStoriesTestDouble) {
		val scope = ProjectSteps.getProjectScope(double)!!
		val controller = scope.get<DeleteSceneController>()
		val repository = scope.get<SceneRepository>()
		val sceneId = runBlocking {
			repository.listAllScenesInProject(Project.Id(scope.projectId)).first().id
		}
		controller.deleteScene(sceneId.uuid.toString())
	}

	fun deletedScene(sceneId: Scene.Id) = object : DependentProperty<Nothing> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps::givenProjectHasBeenOpened
		)

		override fun get(double: SoyleStoriesTestDouble): Nothing? = null

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val controller = scope.get<DeleteSceneController>()
			interact {
				controller.deleteScene(sceneId.uuid.toString())
			}
		}
	}

	fun characterIncludedIn(characterId: Character.Id, sceneId: Scene.Id) = object : DependentProperty<Nothing> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps::givenProjectHasBeenOpened
		)

		override fun get(double: SoyleStoriesTestDouble): Nothing? = null

		override fun check(double: SoyleStoriesTestDouble): Boolean {
			val scope = ProjectSteps.getProjectScope(double) ?: return false
			val scene = runBlocking {
				scope.get<SceneRepository>().getSceneById(sceneId)
			} ?: return false
			return scene.includesCharacter(characterId)
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val scene = runBlocking {
				scope.get<SceneRepository>().getSceneById(sceneId)
			}!!
			val storyEvent = runBlocking {
				scope.get<StoryEventRepository>().getStoryEventById(scene.storyEventId)
			}!!
			interact {
				scope.get<AddCharacterToStoryEventController>().addCharacterToStoryEvent(storyEvent.id.uuid.toString(), characterId.uuid.toString())
			}
		}
	}

	fun charactersMotivationIn(characterId: Character.Id, motivation: String?, sceneId: Scene.Id) = object : DependentProperty<String> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  characterIncludedIn(characterId, sceneId)::given
		)

		override fun get(double: SoyleStoriesTestDouble): String? {
			val scope = ProjectSteps.getProjectScope(double) ?: return null
			val scene = runBlocking {
				scope.get<SceneRepository>().getSceneById(sceneId)
			} ?: return null
			return scene.getMotivationForCharacter(characterId)?.motivation
		}

		override fun check(double: SoyleStoriesTestDouble): Boolean {
			val scope = ProjectSteps.getProjectScope(double) ?: return false
			val scene = runBlocking {
				scope.get<SceneRepository>().getSceneById(sceneId)
			} ?: return false
			return scene.getMotivationForCharacter(characterId)?.motivation == motivation
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			val controller = scope.get<SetMotivationForCharacterInSceneController>()
			interact {
				if (motivation == null) {
					controller.clearMotivationForCharacter(sceneId.uuid.toString(), characterId.uuid.toString())
				} else {
					controller.setMotivationForCharacter(sceneId.uuid.toString(), characterId.uuid.toString(), motivation)
				}
			}
		}
	}

	fun locationLinkedToScene(scene: Scene, location: Location) = object : DependentProperty<Unit> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf()

		override fun get(double: SoyleStoriesTestDouble): Unit? = Unit
		override fun check(double: SoyleStoriesTestDouble): Boolean {
			val repo = ProjectSteps.getProjectScope(double)
			  ?.get<SceneRepository>() ?: return false
			return runBlocking {
				repo.getSceneById(scene.id)?.locationId == location.id
			}
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val controller = ProjectSteps.getProjectScope(double)!!
			  .get<LinkLocationToSceneController>()
			controller.linkLocationToScene(scene.id.uuid.toString(), location.id.uuid.toString())
		}
	}

	fun characterRemovedFrom(scene: Scene, characterId: Character.Id) = object : DependentProperty<Unit> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf()
		override fun get(double: SoyleStoriesTestDouble): Unit? = null
		override fun whenSet(double: SoyleStoriesTestDouble) {
			ProjectSteps.getProjectScope(double)!!
			  .get<RemoveCharacterFromStoryEventController>()
			  .removeCharacter(scene.storyEventId.uuid.toString(), characterId.uuid.toString())
		}
	}
}