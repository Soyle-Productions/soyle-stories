package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.gui.View
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneController
import com.soyle.stories.scene.trackSymbolInScene.ListAvailableSymbolsToTrackInSceneController
import com.soyle.stories.scene.trackSymbolInScene.PinSymbolToSceneController
import com.soyle.stories.scene.trackSymbolInScene.UnpinSymbolFromSceneController
import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme

class SymbolsInSceneController(
    view: View.Nullable<SymbolsInSceneViewModel>,
    private val listSymbolsInSceneController: ListSymbolsInSceneController,
    private val listAvailableSymbolsToTrackInSceneController: ListAvailableSymbolsToTrackInSceneController,
    private val pinSymbolToSceneController: PinSymbolToSceneController,
    private val unpinSymbolFromSceneController: UnpinSymbolFromSceneController
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

    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        if (presenter.view.viewModel?.themesInScene?.any { it.themeId.uuid == renamedTheme.themeId } == true) {
            presenter.receiveRenamedTheme(renamedTheme)
        }
    }

    override suspend fun receiveSymbolPinnedToScene(symbolPinnedToScene: SymbolPinnedToScene) {
        if (presenter.view.viewModel?.targetScene?.id == symbolPinnedToScene.sceneId.uuid.toString()) {
            presenter.receiveSymbolPinnedToScene(symbolPinnedToScene)
        }
    }

    override suspend fun receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene: SymbolUnpinnedFromScene) {
        if (presenter.view.viewModel?.targetScene?.id == symbolUnpinnedFromScene.sceneId.uuid.toString()) {
            presenter.receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene)
        }
    }

    override fun openSceneListTool() {
    }

    override fun getSymbolsInScene(sceneId: Scene.Id) {
        listSymbolsInSceneController.listSymbolsInScene(sceneId, presenter)
    }

    override fun listAvailableSymbolsToTrack(sceneId: Scene.Id) {
        listAvailableSymbolsToTrackInSceneController.listAvailableSymbolsToTrackInScene(sceneId, presenter)
    }

    override fun pinSymbol(sceneId: Scene.Id, symbolId: Symbol.Id) {
        pinSymbolToSceneController.pinSymbolToScene(sceneId, symbolId)
    }

    override fun unpinSymbol(sceneId: Scene.Id, symbolId: Symbol.Id) {
        unpinSymbolFromSceneController.unpinSymbolFromScene(sceneId, symbolId)
    }

}