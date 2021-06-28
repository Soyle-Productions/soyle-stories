package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneReceiver
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene

class SceneCharactersRemovedCharacterPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) : RemovedCharacterFromSceneReceiver {

    override suspend fun receiveRemovedCharacterFromScene(removedCharacterFromScene: RemoveCharacterFromScene.ResponseModel) {
        if (view.viewModel?.targetSceneId?.uuid != removedCharacterFromScene.sceneId) return
        view.updateOrInvalidated {
            copy(
                includedCharacters = includedCharacters?.filterNot { it.id.uuid == removedCharacterFromScene.characterId }
            )
        }
    }

}