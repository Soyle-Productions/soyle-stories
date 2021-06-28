package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.DetectUnusedSymbolsInScene

class DetectedInvalidMentionsPresenter(
    private val view: View.Nullable<SceneListViewModel>
) : DetectInvalidatedMentions.OutputPort, DetectUnusedSymbolsInScene.OutputPort {

    override suspend fun receiveDetectedInvalidatedMentions(response: DetectInvalidatedMentions.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                scenes = scenes.map {
                    if (it.proseId == response.proseId) {
                        it.copy(invalidEntitiesMentioned = response.invalidEntityIds.isNotEmpty())
                    } else it
                }
            )
        }
    }

    override suspend fun receiveDetectedUnusedSymbols(response: DetectUnusedSymbolsInScene.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                scenes = scenes.map {
                    if (it.id == response.sceneId.uuid.toString()) {
                        it.copy(unusedSymbols = response.unusedSymbolIds.isNotEmpty())
                    } else it
                }
            )
        }
    }

}