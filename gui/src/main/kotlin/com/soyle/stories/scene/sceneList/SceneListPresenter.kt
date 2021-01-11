package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.presenters.*
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes
import com.soyle.stories.scene.usecases.renameScene.RenameScene
import com.soyle.stories.scene.usecases.reorderScene.ReorderScene

class SceneListPresenter(
    private val view: View.Nullable<SceneListViewModel>,
    createSceneNotifier: Notifier<CreateNewScene.OutputPort>,
    renameSceneNotifier: Notifier<RenameScene.OutputPort>,
    deleteSceneNotifier: Notifier<DeleteScene.OutputPort>,
    sceneReordered: Notifier<ReorderScene.OutputPort>,
    invalidMentionsNotifier: Notifier<DetectInvalidatedMentions.OutputPort>
) : ListAllScenes.OutputPort {

    private val subPresenters = listOf(
        CreateScenePresenter(view) listensTo createSceneNotifier,
        RenameScenePresenter(view) listensTo renameSceneNotifier,
        DeleteScenePresenter(view) listensTo deleteSceneNotifier,
        ReorderScenePresenter(view) listensTo sceneReordered,
        DetectedInvalidMentionsPresenter(view) listensTo invalidMentionsNotifier
    )

    override fun receiveListAllScenesResponse(response: ListAllScenes.ResponseModel) {
        view.update {
            SceneListViewModel(
                toolTitle = "Scenes",
                emptyLabel = "No Scenes to display",
                createSceneButtonLabel = "Create New Scene",
                scenes = response.scenes.map(::SceneItemViewModel),
                renameSceneFailureMessage = null
            )
        }
    }

}