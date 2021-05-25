package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.domain.prose.events.ContentReplaced
import com.soyle.stories.domain.scene.*
import com.soyle.stories.domain.scene.events.*
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.gui.View
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.prose.editProse.ContentReplacedReceiver
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneController
import com.soyle.stories.scene.trackSymbolInScene.DetectUnusedSymbolsInSceneController
import com.soyle.stories.scene.trackSymbolInScene.ListAvailableSymbolsToTrackInSceneController
import com.soyle.stories.scene.trackSymbolInScene.PinSymbolToSceneController
import com.soyle.stories.scene.trackSymbolInScene.UnpinSymbolFromSceneController
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.DetectUnusedSymbolsInScene
import com.soyle.stories.usecase.theme.changeThemeDetails.RenamedTheme
import java.util.*

class SymbolsInSceneController(
    view: View.Nullable<SymbolsInSceneViewModel>,
    private val openToolController: OpenToolController,
    private val listSymbolsInSceneController: ListSymbolsInSceneController,
    private val listAvailableSymbolsToTrackInSceneController: ListAvailableSymbolsToTrackInSceneController,
    private val pinSymbolToSceneController: PinSymbolToSceneController,
    private val unpinSymbolFromSceneController: UnpinSymbolFromSceneController,
    private val detectUnusedSymbolsController: DetectUnusedSymbolsInSceneController
) : SymbolsInSceneViewListener,
    SymbolsInSceneEventReceiver,
    ContentReplacedReceiver
{

    private val presenter = SymbolsInScenePresenter(view)

    override suspend fun receiveSymbolsTrackedInScene(symbolsTrackedInScene: List<SymbolTrackedInScene>) {
        val sceneId = presenter.view.viewModel?.targetScene?.id ?: return
        val relevantEvents = symbolsTrackedInScene.filter { it.sceneId.uuid.toString() == sceneId }
        if (relevantEvents.isNotEmpty()) {
            presenter.receiveSymbolsTrackedInScene(relevantEvents)
            detectUnusedSymbolsController.detectUnusedSymbols(Scene.Id(UUID.fromString(sceneId)))
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
            detectUnusedSymbolsController.detectUnusedSymbols(Scene.Id(UUID.fromString(sceneId)))
        }
    }

    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        if (presenter.view.viewModel?.themesInScene?.any { it.themeId.uuid == renamedTheme.themeId } == true) {
            presenter.receiveRenamedTheme(renamedTheme)
        }
    }

    override suspend fun receiveSymbolPinnedToScene(symbolPinnedToScene: SymbolPinnedToScene) {
        val sceneId = presenter.view.viewModel?.targetScene?.id ?: return
        if (sceneId == symbolPinnedToScene.sceneId.uuid.toString()) {
            presenter.receiveSymbolPinnedToScene(symbolPinnedToScene)
            detectUnusedSymbolsController.detectUnusedSymbols(Scene.Id(UUID.fromString(sceneId)))
        }
    }

    override suspend fun receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene: SymbolUnpinnedFromScene) {
        val sceneId = presenter.view.viewModel?.targetScene?.id ?: return
        if (sceneId == symbolUnpinnedFromScene.sceneId.uuid.toString()) {
            presenter.receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene)
            detectUnusedSymbolsController.detectUnusedSymbols(Scene.Id(UUID.fromString(sceneId)))
        }
    }

    override suspend fun receiveContentReplacedEvent(contentReplaced: ContentReplaced) {
        val targetScene = presenter.view.viewModel?.targetScene ?: return
        if (targetScene.proseId == contentReplaced.proseId) {
            detectUnusedSymbolsController.detectUnusedSymbols(Scene.Id(UUID.fromString(targetScene.id)))
        }
    }

    override suspend fun receiveDetectedUnusedSymbols(response: DetectUnusedSymbolsInScene.ResponseModel) {
        if (presenter.view.viewModel?.targetScene?.id == response.sceneId.uuid.toString()) {
            presenter.receiveDetectedUnusedSymbols(response)
        }
    }

    override fun openSceneListTool() {
        openToolController.openSceneList()
    }

    override fun getSymbolsInScene(sceneId: Scene.Id) {
        listSymbolsInSceneController.listSymbolsInScene(sceneId, presenter)
            .invokeOnCompletion {
                if (it == null) {
                    detectUnusedSymbolsController.detectUnusedSymbols(sceneId)
                }
            }
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