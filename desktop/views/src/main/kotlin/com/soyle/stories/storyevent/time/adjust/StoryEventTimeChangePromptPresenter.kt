package com.soyle.stories.storyevent.time.adjust

import javafx.stage.Modality
import javafx.stage.Window
import kotlinx.coroutines.CompletableDeferred

class StoryEventTimeChangePromptPresenter(
    private val getOwnerWindow: () -> Window?
) : AdjustStoryEventsTimePrompt {

    private var viewModel = StoryEventTimeChangeViewModel()
    private var view = StoryEventTimeChangeView(viewModel)

    override suspend fun requestAdjustmentAmount(): Long? {
        val deferred = CompletableDeferred<Long?>()
        openWindowIfNotAlready(deferred)
        viewModel.endSubmission()

        viewModel.setOnSubmit {
            if (! deferred.isCompleted) deferred.complete(viewModel.adjustment)
        }
        return deferred.await()
    }

    override suspend fun confirmAdjustmentAmount(amount: Long): Long? {
        val deferred = CompletableDeferred<Long?>()
        openWindowIfNotAlready(deferred)
        viewModel.endSubmission()
        viewModel.adjustment = amount
        viewModel.setOnSubmit {
            if (! deferred.isCompleted) deferred.complete(viewModel.adjustment)
        }
        return deferred.await()
    }

    private fun openWindowIfNotAlready(deferred: CompletableDeferred<Long?>) {
        if (view.currentStage?.isShowing != true) view.openModal(modality = Modality.APPLICATION_MODAL, owner = getOwnerWindow())
        view.currentStage?.setOnHidden {
            if (! deferred.isCompleted) deferred.complete(null)

            viewModel = StoryEventTimeChangeViewModel()
            view = StoryEventTimeChangeView(viewModel)
        }
    }

    override fun close() {
        view.currentStage?.close()
    }

}