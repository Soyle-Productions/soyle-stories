package com.soyle.stories.scene.reorder

import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.PromptChoice
import javafx.stage.Stage
import kotlinx.coroutines.CompletableDeferred

class ReorderScenePromptPresenter : (Scene.Id) -> ReorderScenePrompt {

    override fun invoke(sceneId: Scene.Id): ReorderScenePrompt {

        val viewModel = ReorderScenePromptViewModel()
        val view = ReorderScenePromptView(viewModel)
        viewModel.setOnCancel(view::close)

        return object : ReorderScenePrompt {
            override suspend fun requestConfirmation(sceneName: String): Confirmation<PromptChoice>? {
                val deferred = CompletableDeferred<Confirmation<PromptChoice>?>()

                val stage = showView()
                viewModel.name = sceneName
                viewModel.setOnConfirm {
                    if (!deferred.isCompleted) deferred.complete(Confirmation(it, viewModel.showAgain))
                }
                stage.setOnHidden {
                    if (!deferred.isCompleted) deferred.complete(null)
                }

                return deferred.await()
            }

            override fun close() {
                view.close()
            }

            private fun showView(): Stage {
                return view.currentStage?.takeIf { it.isShowing }
                    ?: view.openModal()!!
            }
        }
    }

}