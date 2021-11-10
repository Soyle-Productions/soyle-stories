package com.soyle.stories.storyevent.create

import com.soyle.stories.domain.validation.NonBlankString
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.CompletableDeferred

class CreateStoryEventPromptPresenter(private val ownerWindow: () -> Window?) : CreateStoryEventController.PropertiesPrompt {

    private val prompt = CreateStoryEventPrompt()
    private var view = CreateStoryEventPromptView(prompt)

    override suspend fun requestNameAndTime(): Pair<NonBlankString, Long>? {
        if (view.currentStage?.isShowing != true) view.openModal(modality = Modality.APPLICATION_MODAL, owner = ownerWindow())
        prompt.isTimeFieldShown = true

        val deferred = CompletableDeferred<Pair<NonBlankString, Long>?>()
        prompt.setOnSubmit {
            if (! deferred.isCompleted) {
                val name = prompt.name ?: return@setOnSubmit
                val time = prompt.time ?: return@setOnSubmit
                deferred.complete(name to time)
            }
        }
        view.currentStage?.completeWhenHidden(deferred)
        return deferred.await()
    }

    override suspend fun requestName(): NonBlankString? {
        if (view.currentStage?.isShowing != true) view.openModal(modality = Modality.APPLICATION_MODAL, owner = ownerWindow())
        prompt.isTimeFieldShown = false

        val deferred = CompletableDeferred<NonBlankString?>()
        prompt.setOnSubmit {
            if (! deferred.isCompleted) {
                val name = prompt.name ?: return@setOnSubmit
                deferred.complete(name)
            }
        }
        view.currentStage?.completeWhenHidden(deferred)
        return deferred.await()
    }

    private fun <T : Any> Stage.completeWhenHidden(deferred: CompletableDeferred<T?>) {
        setOnHidden {
            if (! deferred.isCompleted) { deferred.complete(null) }
            view = CreateStoryEventPromptView(prompt)
        }
    }

    override suspend fun close() {
        view.close()
    }
}