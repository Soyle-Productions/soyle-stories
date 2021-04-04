package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.scene.events.RenamedCharacterInScene
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneReceiver

internal class SceneCharactersRenamedCharacterPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) : RenamedCharacterInSceneReceiver {

    override suspend fun receiveRenamedCharacterInScene(renamedCharacterInScene: RenamedCharacterInScene) {
        if (view.viewModel?.targetSceneId != renamedCharacterInScene.sceneId) return
        view.updateOrInvalidated {
            copy(
                includedCharacters = includedCharacters?.map {
                    if (it.id == renamedCharacterInScene.renamedCharacter.characterId) {
                        it.copy(name = renamedCharacterInScene.renamedCharacter.characterName)
                    } else it
                }
            )
        }
    }
}