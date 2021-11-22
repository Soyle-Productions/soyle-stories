package com.soyle.stories.scene.create

import com.soyle.stories.domain.validation.NonBlankString
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.CompletableDeferred

class CreateScenePromptPresenter(
    private val ownerWindow: () -> Window?
) : CreateScenePrompt {

    private val viewModel = CreateScenePromptViewModel()
    private val view = CreateScenePromptView(viewModel)

    override suspend fun requestSceneName(): NonBlankString? {
        val deferred = CompletableDeferred<NonBlankString?>()

        val stage = showView()
        viewModel.setOnSubmit { if (! deferred.isCompleted) deferred.complete(it) }
        stage.setOnHidden {
            if (! deferred.isCompleted) deferred.complete(null)
            viewModel.reset()
        }

        return deferred.await()
    }

    override fun close() {
        view.close()
    }

    private fun showView(): Stage {
        return view.currentStage?.takeIf { it.isShowing } ?:
            view.openModal(owner = ownerWindow())!!
    }

}