package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.scene.create.SceneCreatedReceiver
import com.soyle.stories.scene.delete.SceneDeletedReceiver
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesReceiver
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.renameScene.SceneRenamedReceiver
import com.soyle.stories.scene.sceneList.presenters.*
import com.soyle.stories.usecase.scene.list.ListAllScenes
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.DetectUnusedSymbolsInScene

class SceneListPresenter(
    private val view: View.Nullable<SceneListViewModel>,
    createSceneNotifier: Notifier<SceneCreatedReceiver>,
    renameSceneNotifier: Notifier<SceneRenamedReceiver>,
    deleteSceneNotifier: Notifier<SceneDeletedReceiver>,
    sceneReordered: Notifier<ReorderScene.OutputPort>,
    invalidMentionsNotifier: Notifier<DetectInvalidatedMentions.OutputPort>,
    unusedSymbolsDetectedNotifier: Notifier<DetectUnusedSymbolsInScene.OutputPort>,
    sceneInconsistenciesNotifier: Notifier<SceneInconsistenciesReceiver>
) : ListAllScenes.OutputPort {

    private val subPresenters = listOf(
        CreateScenePresenter(view) listensTo createSceneNotifier,
        RenameScenePresenter(view) listensTo renameSceneNotifier,
        DeleteScenePresenter(view) listensTo deleteSceneNotifier,
        ReorderScenePresenter(view) listensTo sceneReordered,
        DetectedInvalidMentionsPresenter(view).apply {
            listensTo(unusedSymbolsDetectedNotifier)
            listensTo(invalidMentionsNotifier)
        },
        SceneInconsistenciesPresenter(view) listensTo sceneInconsistenciesNotifier
    )

    override suspend fun receiveListOfScenesInStory(response: ListAllScenes.ListOfScenesInStory) {
        view.update {
            SceneListViewModel(
                toolTitle = "Scenes",
                emptyLabel = "No Scenes to display",
                createSceneButtonLabel = "Create New Scene",
                scenes = response.map(::SceneItemViewModel),
                renameSceneFailureMessage = null
            )
        }
    }
}
