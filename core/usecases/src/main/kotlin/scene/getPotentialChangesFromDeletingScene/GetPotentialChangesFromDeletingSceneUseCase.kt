package com.soyle.stories.usecase.scene.getPotentialChangesFromDeletingScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.common.AffectedCharacter
import com.soyle.stories.usecase.scene.common.AffectedScene
import com.soyle.stories.usecase.scene.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene.*

class GetPotentialChangesFromDeletingSceneUseCase(
  private val sceneRepository: SceneRepository
) : GetPotentialChangesFromDeletingScene {

	override suspend fun invoke(request: RequestModel, output: OutputPort) {
		val response = try {
			execute(request)
		} catch (e: Exception) {
			return output.failedToGetPotentialChangesFromDeletingScene(e)
		}
		output.receivePotentialChangesFromDeletingScene(response)
	}

	private suspend fun execute(request: RequestModel): ResponseModel {
		val scene = getScene(request)
		return if (!scene.hasCharacters() || allMotivesInherited(scene)) {
			ResponseModel(listOf())
		} else {
			getPotentialChanges(scene)
		}
	}

	private suspend fun getPotentialChanges(scene: Scene): ResponseModel {
		val charactersWithMotivation = getCharacterMotivationsSetInScene(scene)
		val (scenesBefore, scenesAfter) = getScenesBeforeAndAfter(scene)
		val affectedScenes = getAffectedScenes(charactersWithMotivation, scenesBefore, scenesAfter)
		return ResponseModel(affectedScenes)
	}

	private tailrec fun getAffectedScenes(
	  potentiallyAffectedCharacters: List<Scene.CharacterMotivation>,
	  scenesBefore: List<Scene>,
	  scenesAfter: List<Scene>,
	  affectedScenes: List<AffectedScene> = listOf()
	): List<AffectedScene> {

		if (scenesAfter.isEmpty()) return affectedScenes
		val scene = scenesAfter.first()

		return getAffectedScenes(
		  potentiallyAffectedCharacters.filterNot { scene.setsMotivationForCharacter(it.characterId) },
		  scenesBefore,
		  scenesAfter.drop(1),
		  if (scene.inheritsFromAny(potentiallyAffectedCharacters)) {
			  affectedScenes + getAffect(scene, potentiallyAffectedCharacters, scenesBefore)
		  } else affectedScenes
		)
	}

	private fun getAffect(scene: Scene, originalMotivations: List<Scene.CharacterMotivation>, scenesBefore: List<Scene>): AffectedScene
	{
		val inheritedMotives = originalMotivations.filter {
			scene.inheritsMotivationForCharacter(it.characterId)
		}
		return AffectedScene(
		  scene.id.uuid,
		  scene.name.value,
		  inheritedMotives.map { originalMotive ->
			  AffectedCharacter(
				originalMotive.characterId.uuid,
				originalMotive.characterName,
				originalMotive.motivation ?: "",
				scenesBefore.asReversed().find {
					it.setsMotivationForCharacter(originalMotive.characterId)
				}?.getMotivationForCharacter(originalMotive.characterId)?.motivation ?: ""
			  )
		  }
		)
	}

	private fun Scene.inheritsFromAny(motivesFromWhichToInherit: List<Scene.CharacterMotivation>): Boolean
	{
		return hasCharacters() && motivesFromWhichToInherit.any {
			inheritsMotivationForCharacter(it.characterId)
		}
	}

	private fun Scene.setsMotivationForCharacter(characterId: Character.Id): Boolean
	{
		return includesCharacter(characterId) &&
		  !getMotivationForCharacter(characterId)!!.isInherited()
	}

	private fun Scene.inheritsMotivationForCharacter(characterId: Character.Id): Boolean
	{
		return includesCharacter(characterId) &&
		  getMotivationForCharacter(characterId)!!.isInherited()
	}

	private fun getCharacterMotivationsSetInScene(scene: Scene) =
	  scene.includedCharacters.asSequence()
		  .map { scene.getMotivationForCharacter(it.characterId)!! }
		.filterNot { it.isInherited() }.toList()

	private suspend fun getScenesBeforeAndAfter(scene: Scene): Pair<List<Scene>, List<Scene>> {
		val sceneOrder = sceneRepository.getSceneIdsInOrder(scene.projectId)!!.order.withIndex().associate { it.value to it.index }
		val scenes = sceneRepository.listAllScenesInProject(scene.projectId).sortedBy {
			sceneOrder.getValue(it.id)
		}
		return scenes.subList(0, sceneOrder.getValue(scene.id)) to scenes.subList(sceneOrder.getValue(scene.id) + 1, scenes.size)
	}

	private suspend fun getScene(request: RequestModel) =
	  (sceneRepository.getSceneById(Scene.Id(request.sceneId))
		?: throw SceneDoesNotExist(request.locale, request.sceneId))

	private fun allMotivesInherited(scene: Scene): Boolean {
		return scene.includedCharacters.all { scene.getMotivationForCharacter(it.characterId)!!.isInherited() }
	}
}