package com.soyle.stories.storyevent.time

import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimePrompt
import com.soyle.stories.storyevent.time.adjust.AdjustTimePromptViewModel
import com.soyle.stories.storyevent.time.reschedule.ReschedulePromptViewModel
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventPrompt
import javafx.beans.binding.BooleanExpression
import javafx.stage.Modality
import javafx.stage.Window
import kotlinx.coroutines.CompletableDeferred
import tornadofx.booleanBinding
import tornadofx.booleanProperty

class StoryEventTimeChangePromptPresenter(
    private val getOwnerWindow: () -> Window?
) : AdjustStoryEventsTimePrompt, RescheduleStoryEventPrompt {

    private var viewModel: StoryEventTimeChangeViewModel? = null
    private var view: StoryEventTimeChangeView? = null

    override suspend fun requestAdjustmentAmount(): Long? {
        val deferred = CompletableDeferred<Long?>()
        val viewModel = adjustmentViewModel()
        openWindowIfNotAlready(deferred)
        viewModel.endSubmission()

        viewModel.setOnSubmit {
            if (! deferred.isCompleted) deferred.complete(viewModel.time)
        }
        return deferred.await()
    }

    override suspend fun confirmAdjustmentAmount(amount: Long): Long? {
        val deferred = CompletableDeferred<Long?>()
        val viewModel = adjustmentViewModel()
        openWindowIfNotAlready(deferred)
        viewModel.endSubmission()
        viewModel.time = amount
        viewModel.setOnSubmit {
            if (! deferred.isCompleted) deferred.complete(viewModel.time)
        }
        return deferred.await()
    }

    override suspend fun requestNewTime(currentTime: Long): Long? {
        val deferred = CompletableDeferred<Long?>()
        val viewModel = rescheduleViewModel(currentTime)
        openWindowIfNotAlready(deferred)
        viewModel.endSubmission()
        viewModel.setOnSubmit {
            if (! deferred.isCompleted) deferred.complete(viewModel.time)
        }
        return deferred.await()
    }

    private fun adjustmentViewModel(): StoryEventTimeChangeViewModel {
        var viewModel = viewModel as? AdjustTimePromptViewModel
        if (viewModel == null) {
            viewModel = AdjustTimePromptViewModel()
            view = StoryEventTimeChangeView(viewModel)
            this.viewModel = viewModel
        }
        return viewModel
    }

    private fun rescheduleViewModel(currentTime: Long): StoryEventTimeChangeViewModel {
        var viewModel = viewModel as? ReschedulePromptViewModel
        if (viewModel == null) {
            viewModel = ReschedulePromptViewModel(currentTime)
            view = StoryEventTimeChangeView(viewModel)
            this.viewModel = viewModel
        }
        return viewModel
    }

    private fun openWindowIfNotAlready(deferred: CompletableDeferred<Long?>) {
        if (view?.currentStage?.isShowing != true) view?.openModal(modality = Modality.APPLICATION_MODAL, owner = getOwnerWindow())
        view?.currentStage?.setOnHidden {
            if (! deferred.isCompleted) deferred.complete(null)

            viewModel = null
            view = null
        }
    }

    override fun close() {
        view?.currentStage?.close()
    }

}