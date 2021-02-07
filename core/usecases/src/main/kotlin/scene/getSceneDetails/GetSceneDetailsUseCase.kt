package com.soyle.stories.usecase.scene.getSceneDetails

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene
import com.soyle.stories.usecase.scene.common.getLastSetMotivation
import com.soyle.stories.usecase.scene.common.getScenesBefore

class GetSceneDetailsUseCase(
  private val sceneRepository: SceneRepository,
  private val characterArcRepository: CharacterArcRepository
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
            scene.settings.firstOrNull()?.uuid,
		  getIncludedCharacterDetails(scene)
		)
	}

	private suspend fun getScene(request: GetSceneDetails.RequestModel) =
	  sceneRepository.getSceneById(Scene.Id(request.sceneId))
		?: throw SceneDoesNotExist(request.locale, request.sceneId)

	private suspend fun getIncludedCharacterDetails(scene: Scene): List<IncludedCharacterInScene>
	{
		val scenesBefore = getScenesBefore(scene, sceneRepository).asReversed()

		// for each covered arc section in the scene, get the character arc
		val arcs = characterArcRepository.getCharacterArcsContainingArcSections(scene.coveredArcSectionIds.toSet())
		// associate each arc section in each arc and the arc itself by the arc section id for easy look-up
		val arcSectionsWithArc = arcs.flatMap { arc -> arc.arcSections.map { it to arc } }.associateBy { it.first.id }

		return scene.includedCharacters.map {
			val motivation = scene.getMotivationForCharacter(it.characterId)!!
			IncludedCharacterInScene(
				scene.id.uuid, it.characterId.uuid, it.characterName, motivation.motivation,
			  getLastSetMotivation(scenesBefore, it.characterId),
				(scene.getCoveredCharacterArcSectionsForCharacter(it.characterId) ?: listOf()).map { arcSectionId ->
					val (arcSection, arc) = arcSectionsWithArc.getValue(arcSectionId)
					CoveredArcSectionInScene(
						arcSection.id.uuid,
						arcSection.template.name,
						arcSection.value,
						arcSection.template.allowsMultiple,
						arc.id.uuid,
						arc.name
					)
				}
			)
		}
	}


}