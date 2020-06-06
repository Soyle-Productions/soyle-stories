package com.soyle.stories.scene.usecases.getSceneDetails

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.repositories.SceneRepository

class GetSceneDetailsUseCase(
  private val sceneRepository: SceneRepository
) : GetSceneDetails {

	override suspend fun invoke(request: GetSceneDetails.RequestModel, output: GetSceneDetails.OutputPort) {
		val response = try { execute(request) }
		catch (e: Exception) { return output.failedToGetSceneDetails(e) }
		output.sceneDetailsRetrieved(response)
	}

	private suspend fun execute(request: GetSceneDetails.RequestModel): GetSceneDetails.ResponseModel
	{
		val scene = getScene(request)
		return GetSceneDetails.ResponseModel(
		  scene.id.uuid,
		  scene.locationId?.uuid,
		  getIncludedCharacterDetails(scene)
		)
	}

	private suspend fun getScene(request: GetSceneDetails.RequestModel) =
	  sceneRepository.getSceneById(Scene.Id(request.sceneId))
		?: throw SceneDoesNotExist(request.locale, request.sceneId)

	private suspend fun getIncludedCharacterDetails(scene: Scene): List<GetSceneDetails.IncludedCharacterDetails>
	{
		val scenesBefore = getScenesBefore(scene).asReversed()
		return scene.characterMotivations.map {
			GetSceneDetails.IncludedCharacterDetails(it.characterId.uuid, it.characterName, it.motivation,
			  getLastSetMotivation(scenesBefore, it.characterId)
			)
		}
	}

	private suspend fun getScenesBefore(scene: Scene): List<Scene> {
		return sceneRepository.listAllScenesInProject(scene.projectId)
		  .sortedByProjectOrder(scene.projectId)
		  .takeWhile { it.id != scene.id }
	}

	private suspend fun List<Scene>.sortedByProjectOrder(projectId: Project.Id): List<Scene> {
		val indexOf = sceneRepository.getSceneIdsInOrder(projectId)
		  .withIndex()
		  .associate { it.value to it.index }
		return sortedBy { indexOf.getValue(it.id) }
	}

	private fun getLastSetMotivation(
	  reversedScenesBefore: List<Scene>, characterId: Character.Id
	): GetSceneDetails.InheritedMotivation?
	{
		val lastSetScene = reversedScenesBefore.find {
			it.includesCharacter(characterId) && !it.getMotivationForCharacter(characterId)!!.isInherited()
		} ?: return null
		return GetSceneDetails.InheritedMotivation(
		  lastSetScene.id.uuid,
		  lastSetScene.name,
		  lastSetScene.getMotivationForCharacter(characterId)!!.motivation!!
		)
	}


}