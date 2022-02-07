package com.soyle.stories.scene.reorderSceneRamifications

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Receiver
import com.soyle.stories.common.listensTo
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.gui.View
import com.soyle.stories.scene.delete.SceneDeletedReceiver
import com.soyle.stories.scene.deleteSceneRamifications.CharacterRamificationsViewModel
import com.soyle.stories.scene.deleteSceneRamifications.SceneRamificationsViewModel
import com.soyle.stories.usecase.scene.reorderScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.reorderScene.PotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class ReorderSceneRamificationsPresenter(
    private val view: View.Nullable<ReorderSceneRamificationsViewModel>,
    sceneDeleted: Notifier<SceneDeletedReceiver>,
    characterRemoved: Notifier<Receiver<CharacterRemovedFromScene>>,
    characterMotivationSet: Notifier<SetMotivationForCharacterInScene.OutputPort>
) :
    GetPotentialChangesFromReorderingScene.OutputPort,
    SceneDeletedReceiver,
    Receiver<CharacterRemovedFromScene>,
    SetMotivationForCharacterInScene.OutputPort {

    init {
        this listensTo sceneDeleted
        this listensTo characterRemoved
        this listensTo characterMotivationSet
    }

    override suspend fun receivePotentialChangesFromReorderingScene(response: PotentialChangesFromReorderingScene) {
        view.update {
            ReorderSceneRamificationsViewModel(
                invalid = false,
                okMessage = "",
                scenes = response.affectedScenes.map {
                    SceneRamificationsViewModel(it.sceneName, it.sceneId.toString(), it.characters.map {
                        CharacterRamificationsViewModel(
                            it.characterName,
                            it.characterId.toString(),
                            it.currentMotivation,
                            it.potentialMotivation
                        )
                    })
                }
            )
        }
    }

    override suspend fun receiveSceneDeleted(event: SceneRemoved) {
        view.updateOrInvalidated { copy(invalid = true) }
    }

    override suspend fun receiveEvent(event: CharacterRemovedFromScene) {
        view.updateOrInvalidated { copy(invalid = true) }
    }

    override suspend fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
        view.updateOrInvalidated { copy(invalid = true) }
    }
}
