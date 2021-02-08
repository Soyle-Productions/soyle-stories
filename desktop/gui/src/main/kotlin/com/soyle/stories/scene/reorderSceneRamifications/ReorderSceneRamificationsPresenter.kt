package com.soyle.stories.scene.reorderSceneRamifications

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.scene.deleteSceneRamifications.CharacterRamificationsViewModel
import com.soyle.stories.scene.deleteSceneRamifications.SceneRamificationsViewModel
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.PotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class ReorderSceneRamificationsPresenter(
    private val view: View.Nullable<ReorderSceneRamificationsViewModel>,
    sceneDeleted: Notifier<DeleteScene.OutputPort>,
    characterRemoved: Notifier<RemoveCharacterFromScene.OutputPort>,
    characterMotivationSet: Notifier<SetMotivationForCharacterInScene.OutputPort>
) :
    GetPotentialChangesFromReorderingScene.OutputPort,
    DeleteScene.OutputPort,
    RemoveCharacterFromScene.OutputPort,
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

    override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
        view.updateOrInvalidated { copy(invalid = true) }
    }

    override fun characterRemovedFromScene(response: RemoveCharacterFromScene.ResponseModel) {
        view.updateOrInvalidated { copy(invalid = true) }
    }

    override fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
        view.updateOrInvalidated { copy(invalid = true) }
    }

    override fun receiveDeleteSceneFailure(failure: Exception) {}
    override fun failedToRemoveCharacterFromScene(failure: Exception) {}
    override fun failedToSetMotivationForCharacterInScene(failure: Exception) {}
}