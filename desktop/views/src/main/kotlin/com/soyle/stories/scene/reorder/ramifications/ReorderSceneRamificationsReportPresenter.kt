package com.soyle.stories.scene.reorder.ramifications

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.reorder.ReorderSceneRamificationsReport
import com.soyle.stories.usecase.scene.reorderScene.PotentialChangesFromReorderingScene
import kotlinx.coroutines.CompletableDeferred
import tornadofx.FX

class ReorderSceneRamificationsReportPresenter(
    private val openToolController: OpenToolController,
    private val projectScope: ProjectScope
) : (Scene.Id) -> ReorderSceneRamificationsReport {

    override fun invoke(sceneId: Scene.Id): ReorderSceneRamificationsReport {

        val viewModel = ReorderSceneRamificationsReportViewModel()
        val view = ReorderSceneRamificationsReportView(viewModel)
        val continuation = CompletableDeferred<Unit?>()
        initializeViewModel(viewModel, view, continuation)

        FX.getComponents(projectScope)[ReorderSceneRamificationsReportView::class] = view
        openToolController.openReorderSceneRamificationsTool(sceneId.uuid.toString())

        return object : ReorderSceneRamificationsReport {
            override suspend fun receivePotentialChangesFromReorderingScene(response: PotentialChangesFromReorderingScene) {
                viewModel.scenes.setAll(response.affectedScenes)
            }

            override suspend fun requestContinuation(): Unit? {
                return continuation.await()
            }
        }

    }

    private fun initializeViewModel(
        viewModel: ReorderSceneRamificationsReportViewModel,
        view: ReorderSceneRamificationsReportView,
        continuation: CompletableDeferred<Unit?>
    ) {
        viewModel.setOnReorder {
            if (! continuation.isCompleted) continuation.complete(Unit)
        }
        viewModel.setOnCancel {
            if (! continuation.isCompleted) continuation.complete(null)
            view.close()
        }
    }

}