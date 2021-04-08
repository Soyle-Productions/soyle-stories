package com.soyle.stories.usecase.scene.character.listIncluded

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene
import com.soyle.stories.usecase.scene.common.PreviousMotivations
import java.util.*

class ListCharactersInSceneUseCase(
    private val sceneRepository: SceneRepository
) : ListCharactersInScene {
    override suspend fun invoke(sceneId: Scene.Id, output: ListCharactersInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)

        val previousMotivations = PreviousMotivations(scene, sceneRepository)

        output.receiveCharactersInScene(ListCharactersInScene.ResponseModel(Scene.Id(), scene.includedCharacters.map {
            IncludedCharacterInScene(
                scene.id,
                it.characterId,
                it.characterName,
                it.roleInScene,
                it.motivation,
                previousMotivations.getLastSetMotivation(it.characterId),
                listOf()
            )
        }))
    }
}