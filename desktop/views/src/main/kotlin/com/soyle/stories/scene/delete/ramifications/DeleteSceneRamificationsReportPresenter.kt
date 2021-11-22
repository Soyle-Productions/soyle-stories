package com.soyle.stories.scene.delete.ramifications

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.delete.DeleteSceneController
import com.soyle.stories.scene.delete.DeleteSceneRamificationsReport
import com.soyle.stories.usecase.scene.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import tornadofx.FX

class DeleteSceneRamificationsReportPresenter(
    private val openToolController: OpenToolController,
    private val projectScope: ProjectScope
) : (Scene.Id) -> DeleteSceneRamificationsReport {

    override fun invoke(sceneId: Scene.Id): DeleteSceneRamificationsReport {

        val viewModel = DeleteSceneRamificationsReportViewModel()
        val view = DeleteSceneRamificationsReportView(viewModel)
        val continuation = CompletableDeferred<Unit?>()
        initializeViewModel(viewModel, view, continuation)

        FX.getComponents(projectScope)[DeleteSceneRamificationsReportView::class] = view
        openToolController.openDeleteSceneRamificationsTool(sceneId.uuid.toString())

        return object : DeleteSceneRamificationsReport {
            override fun receivePotentialChangesFromDeletingScene(response: GetPotentialChangesFromDeletingScene.ResponseModel) {
                viewModel.affectedScenes.setAll(response.affectedScenes)
            }

            override fun failedToGetPotentialChangesFromDeletingScene(failure: Exception) {
                throw failure
            }

            override suspend fun requestContinuation(): Unit? {
                return continuation.await()
            }
        }
    }

    private fun initializeViewModel(
        viewModel: DeleteSceneRamificationsReportViewModel,
        view: DeleteSceneRamificationsReportView,
        continuation: CompletableDeferred<Unit?>
    ) {
        viewModel.setOnDelete {
            if (! continuation.isCompleted) continuation.complete(Unit)
        }
        viewModel.setOnCancel {
            if (! continuation.isCompleted) continuation.complete(null)
            view.close()
        }
    }

}