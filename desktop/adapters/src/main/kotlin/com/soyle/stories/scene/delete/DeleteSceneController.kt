package com.soyle.stories.scene.delete

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
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

            prompt: DeleteScenePrompt,

            deleteScene: DeleteScene,
            deleteSceneOutput: DeleteScene.OutputPort,
            setDialogPreferences: SetDialogPreferences,
            setDialogPreferencesOutput: SetDialogPreferences.OutputPort
        ): DeleteSceneController = object : DeleteSceneController, CoroutineScope by CoroutineScope(guiContext) {
            override fun deleteScene(sceneId: Scene.Id): Job = launch {
                if (dialogPreferences(DialogType.DeleteScene).shouldShow) {
                    val confirmed = prompt.requestConfirmation() ?: return@launch
                    val shouldShowNextTime = prompt.requestShouldConfirmNextTime()
                    if (confirmed) {
                        withContext(asyncContext) {
                            deleteScene.invoke(sceneId, deleteSceneOutput)
                        }
                    }
                    if (shouldShowNextTime != null) {
                        withContext(asyncContext) {
                            setDialogPreferences(DialogType.DeleteScene, shouldShowNextTime, setDialogPreferencesOutput)
                        }
                    }
                } else {
                    withContext(asyncContext) {
                        deleteScene.invoke(sceneId, deleteSceneOutput)
                    }
                }

            }

        }
    }

}