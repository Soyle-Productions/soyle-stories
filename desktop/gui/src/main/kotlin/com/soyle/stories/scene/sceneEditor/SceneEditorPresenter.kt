package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.gui.View
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrame

internal class SceneEditorPresenter(
    private val view: View.Nullable<SceneEditorViewModel>
) : GetSceneFrame.OutputPort {

    override suspend fun receiveSceneFrame(response: GetSceneFrame.ResponseModel) {
        view.update {
            SceneEditorViewModel(
                conflict = response.sceneConflict,
                resolution = response.sceneResolution
            )
        }
    }

}