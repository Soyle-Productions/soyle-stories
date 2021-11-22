package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.gui.View
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesReceiver
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies

class SceneInconsistenciesPresenter(
    private val view: View<SceneListViewModel>
) : SceneInconsistenciesReceiver {

    override suspend fun receiveSceneInconsistencies(sceneInconsistencies: SceneInconsistencies) {
        view.updateOrInvalidated {
            copy(
                scenes = scenes.map {
                    if (it.id != sceneInconsistencies.sceneId) it
                    else {
                        it.copy(
                            inconsistentSettings = sceneInconsistencies
                                .filterIsInstance<SceneInconsistencies.SceneInconsistency.SceneSettingInconsistency>()
                                .firstOrNull()?.run {
                                    any { it.isNotEmpty() }
                                } ?: it.inconsistentSettings
                        )
                    }
                }
            )
        }
    }
}