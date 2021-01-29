package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.SymbolTrackedInScene
import com.soyle.stories.entities.TrackedSymbolRemoved
import com.soyle.stories.entities.TrackedSymbolRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneController

class SymbolsInSceneController(
    view: View.Nullable<SymbolsInSceneViewModel>,
    private val listSymbolsInSceneController: ListSymbolsInSceneController
) : SymbolsInSceneViewListener,
    SymbolsInSceneEventReceiver
{

    private val presenter = SymbolsInScenePresenter(view)

    override suspend fun receiveSymbolsTrackedInScene(symbolsTrackedInScene: List<SymbolTrackedInScene>) {
        val sceneId = presenter.view.viewModel?.targetScene?.id ?: return
        val relevantEvents = symbolsTrackedInScene.filter { it.sceneId.uuid.toString() == sceneId }
        if (relevantEvents.isNotEmpty()) {
            presenter.receiveSymbolsTrackedInScene(relevantEvents)
        }
    }

    override suspend fun receiveTrackedSymbolsRenamed(trackedSymbolsRenamed: List<TrackedSymbolRenamed>) {
        val sceneId = presenter.view.viewModel?.targetScene?.id ?: return
        val relevantEvents = trackedSymbolsRenamed.filter { it.sceneId.uuid.toString() == sceneId }
        if (relevantEvents.isNotEmpty()) {
            presenter.receiveTrackedSymbolsRenamed(relevantEvents)
        }
    }

    override suspend fun receiveTrackedSymbolsRemoved(trackedSymbolsRemoved: List<TrackedSymbolRemoved>) {
        val sceneId = presenter.view.viewModel?.targetScene?.id ?: return
        val relevantEvents = trackedSymbolsRemoved.filter { it.sceneId.uuid.toString() == sceneId }
        if (relevantEvents.isNotEmpty()) {
            presenter.receiveTrackedSymbolsRemoved(relevantEvents)
        }
    }

    override fun openSceneListTool() {
    }

    override fun getSymbolsInScene(sceneId: Scene.Id) {
        listSymbolsInSceneController.listSymbolsInScene(sceneId, presenter)
    }

}