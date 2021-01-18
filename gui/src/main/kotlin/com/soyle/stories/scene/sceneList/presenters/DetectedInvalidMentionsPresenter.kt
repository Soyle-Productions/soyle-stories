package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.scene.sceneList.SceneListViewModel

class DetectedInvalidMentionsPresenter(
    private val view: View.Nullable<SceneListViewModel>
) : DetectInvalidatedMentions.OutputPort {

    override suspend fun receiveDetectedInvalidatedMentions(response: DetectInvalidatedMentions.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                scenes = scenes.map {
                    if (it.proseId == response.proseId) {
                        it.copy(hasProblem = response.invalidEntityIds.isNotEmpty())
                    } else it
                }
            )
        }
    }

}