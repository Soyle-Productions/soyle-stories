package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.character.Character
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludedCharacterInSceneReceiver
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene

class SceneCharactersIncludedCharacterPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) : IncludedCharacterInSceneReceiver {

    override suspend fun receiveIncludedCharacterInScene(includedCharacterInScene: IncludedCharacterInScene) {
        view.updateOrInvalidated {
            if (this.targetSceneId?.uuid != includedCharacterInScene.sceneId) return@updateOrInvalidated this
            copy(
                includedCharacters = includedCharacters?.plus(
                    SceneCharactersPresenter.includedCharacterViewModel(
                        includedCharacterInScene
                    )
                )
            )
        }
    }

}