package com.soyle.stories.scene.reorderSceneRamifications

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneReceiver
import com.soyle.stories.scene.deleteScene.SceneDeletedReceiver
import com.soyle.stories.scene.deleteSceneRamifications.CharacterRamificationsViewModel
import com.soyle.stories.scene.deleteSceneRamifications.SceneRamificationsViewModel
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.PotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class ReorderSceneRamificationsPresenter(
    private val view: View.Nullable<ReorderSceneRamificationsViewModel>,
    sceneDeleted: Notifier<SceneDeletedReceiver>,
    characterRemoved: Notifier<RemovedCharacterFromSceneReceiver>,
    characterMotivationSet: Notifier<SetMotivationForCharacterInScene.OutputPort>
) :
    GetPotentialChangesFromReorderingScene.OutputPort,
    SceneDeletedReceiver,
    RemovedCharacterFromSceneReceiver,
    SetMotivationForCharacterInScene.OutputPort {

    init {
        this listensTo sceneDeleted
        this listensTo characterRemoved
        this listensTo characterMotivationSet
    }

    override fun receivePotentialChangesFromReorderingScene(response: PotentialChangesFromReorderingScene) {
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

    override suspend fun receiveSceneDeleted(event: Scene.Id) {
        view.updateOrInvalidated { copy(invalid = true) }
    }

    override suspend fun receiveRemovedCharacterFromScene(
        removedCharacterFromScene: RemoveCharacterFromScene.ResponseModel
    ) {
        view.updateOrInvalidated { copy(invalid = true) }
    }

    override fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
        view.updateOrInvalidated { copy(invalid = true) }
    }

    override fun failedToSetMotivationForCharacterInScene(failure: Exception) {}
}
