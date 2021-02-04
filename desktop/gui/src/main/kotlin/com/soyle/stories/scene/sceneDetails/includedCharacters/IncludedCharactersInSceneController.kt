package com.soyle.stories.scene.sceneDetails.includedCharacters

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.usecases.includeCharacterInScene.GetAvailableCharactersToAddToScene
import java.util.*

class IncludedCharactersInSceneController(
    private val sceneId: String,
    private val includeCharacterInSceneController: IncludeCharacterInSceneController,
    private val threadTransformer: ThreadTransformer,
    private val getAvailableCharactersToAddToScene: GetAvailableCharactersToAddToScene,
    private val presenter: IncludedCharactersInScenePresenter
) : IncludedCharactersInSceneViewListener {

    override fun getAvailableCharacters() {
        val sceneId = UUID.fromString(sceneId)
        threadTransformer.async {
            getAvailableCharactersToAddToScene.invoke(sceneId, presenter)
        }
    }

    override fun addCharacter(characterId: String) {
        includeCharacterInSceneController.includeCharacterInScene(sceneId, characterId)
    }


}