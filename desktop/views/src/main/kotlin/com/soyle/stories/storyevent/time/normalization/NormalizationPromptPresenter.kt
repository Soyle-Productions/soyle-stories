package com.soyle.stories.storyevent.time.normalization

import com.soyle.stories.storyevent.time.NormalizationPrompt
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.CompletableDeferred

class NormalizationPromptPresenter(private val ownerWindow: () -> Window?) : NormalizationPrompt {

    private var onSubmit: () -> Unit = {}
    private fun submit() {
        onSubmit.invoke()
    }
    private var view = NormalizationPromptView(::submit)


    override suspend fun confirmNormalization(): Boolean {
        if (view.currentStage?.isShowing != true) view.openModal(modality = Modality.APPLICATION_MODAL, owner = ownerWindow())

        val deferred = CompletableDeferred<Boolean>()
        onSubmit = {
            if (! deferred.isCompleted) {
                deferred.complete(true)
            }
            view.close()
        }
        view.currentStage?.completeWhenHidden(deferred)
        return deferred.await()
    }

    private fun Stage.completeWhenHidden(deferred: CompletableDeferred<Boolean>) {
        setOnHidden {
            if (! deferred.isCompleted) { deferred.complete(false) }
            view = NormalizationPromptView(::submit)
        }
    }
}