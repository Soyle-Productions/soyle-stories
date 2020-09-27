package com.soyle.stories.scene.usecases.getSceneDetails

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.usecases.common.IncludedCharacterInScene
import com.soyle.stories.scene.usecases.common.getLastSetMotivation
import com.soyle.stories.scene.usecases.common.getScenesBefore
import java.util.*

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
		  scene.storyEventId.uuid,
		  scene.locationId?.uuid,
		  getIncludedCharacterDetails(scene)
		)
	}

	private suspend fun getScene(request: GetSceneDetails.RequestModel) =
	  sceneRepository.getSceneById(Scene.Id(request.sceneId))
		?: throw SceneDoesNotExist(request.locale, request.sceneId)

	private suspend fun getIncludedCharacterDetails(scene: Scene): List<IncludedCharacterInScene>
	{
		val scenesBefore = getScenesBefore(scene, sceneRepository).asReversed()
		return scene.includedCharacters.map {
			val motivation = scene.getMotivationForCharacter(it.characterId)!!
			IncludedCharacterInScene(
				scene.id.uuid, it.characterId.uuid, it.characterName, motivation.motivation,
			  getLastSetMotivation(scenesBefore, it.characterId)
			)
		}
	}


}