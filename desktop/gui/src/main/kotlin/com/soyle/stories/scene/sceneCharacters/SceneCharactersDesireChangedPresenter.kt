package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.scene.events.CharacterDesireInSceneChanged
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.setDesire.CharacterDesireInSceneChangedReceiver

class SceneCharactersDesireChangedPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) : CharacterDesireInSceneChangedReceiver {

    override suspend fun receiveCharacterDesireInSceneChanged(event: CharacterDesireInSceneChanged) {
        view.updateIf({ targetSceneId == event.sceneId }) {
            copy(includedCharacters = includedCharacters?.map {
                if (it.id == event.characterId) it.copy(desire = event.newDesire)
                else it
            })
        }
    }
}