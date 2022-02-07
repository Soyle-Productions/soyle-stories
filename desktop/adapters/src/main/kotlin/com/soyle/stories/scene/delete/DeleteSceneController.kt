package com.soyle.stories.scene.delete

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.scene.PromptChoice
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.delete.DeleteScene
import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingScene
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface DeleteSceneController {

    fun deleteScene(sceneId: Scene.Id): Job

    companion object {
        fun Implementation(
            guiContext: CoroutineContext,
            asyncContext: CoroutineContext,

            dialogPreferences: suspend (DialogType) -> DialogPreference,

            getPromptForScene: (Scene.Id) -> DeleteScenePrompt,
            getReportForScene: (Scene.Id) -> DeleteSceneRamificationsReport,

            sceneRepository: SceneRepository,

            deleteScene: DeleteScene,
            deleteSceneOutput: DeleteScene.OutputPort,

            setDialogPreferences: SetDialogPreferences,
            setDialogPreferencesOutput: SetDialogPreferences.OutputPort,

            getPotentialChangesFromDeletingScene: GetPotentialChangesFromDeletingScene,
        ): DeleteSceneController = object : DeleteSceneController, CoroutineScope by CoroutineScope(guiContext) {

            override fun deleteScene(sceneId: Scene.Id): Job = launch {
                val preferences = dialogPreferences(DialogType.DeleteScene)
                if (preferences.shouldShow) {
                    showPrompt(sceneId)
                } else {
                    deleteSceneConfirmed(sceneId)
                }

            }

            private suspend fun showPrompt(sceneId: Scene.Id) {
                val scene = withContext(asyncContext) {
                    sceneRepository.getSceneOrError(sceneId.uuid)
                }
                val prompt = getPromptForScene(sceneId)
                prompt.use {
                    val confirmation = prompt.requestConfirmation(scene.name.value) ?: return
                    when (confirmation.choice) {
                        PromptChoice.Confirm -> deleteSceneConfirmed(sceneId)
                        PromptChoice.ShowRamifications -> showRamifications(sceneId)
                    }
                    updateDialogPreferences(confirmation.showAgain)
                }
            }

            private suspend fun deleteSceneConfirmed(sceneId: Scene.Id) {
                withContext(asyncContext) {
                    deleteScene.invoke(sceneId, deleteSceneOutput)
                }
            }

            private suspend fun showRamifications(sceneId: Scene.Id) {
                val report = getReportForScene(sceneId)
                withContext(asyncContext) {
                    getPotentialChangesFromDeletingScene.invoke(sceneId, report)
                }
                report.requestContinuation() ?: return
                deleteSceneConfirmed(sceneId)
            }

            private suspend fun updateDialogPreferences(showAgain: Boolean) {
                setDialogPreferences(
                    DialogType.DeleteScene,
                    showAgain,
                    setDialogPreferencesOutput
                )
            }
        }
    }
}