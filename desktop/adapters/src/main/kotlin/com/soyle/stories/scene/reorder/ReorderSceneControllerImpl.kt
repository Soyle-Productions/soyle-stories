package com.soyle.stories.scene.reorder

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReorderSceneControllerImpl(
    threadTransformer: ThreadTransformer,

    private val dialogPreferences: suspend (DialogType) -> DialogPreference,

    private val prompt: ReorderScenePrompt,

    private val reorderScene: ReorderScene,
    private val reorderSceneOutputPort: ReorderScene.OutputPort,
    private val setDialogPreferences: SetDialogPreferences,
    private val setDialogPreferencesOutput: SetDialogPreferences.OutputPort
) : ReorderSceneController, CoroutineScope by CoroutineScope(threadTransformer.guiContext) {

    private val asyncContext = threadTransformer.asyncContext

    override fun reorderScene(sceneId: Scene.Id, newIndex: Int): Job {
        return launch {
            if (dialogPreferences(DialogType.ReorderScene).shouldShow) {
                val confirmation = prompt.requestConfirmation() ?: return@launch
                val shouldShowNextTime = prompt.requestShouldShowNextTime()
                if (confirmation) {
                    withContext(asyncContext) {
                        reorderScene.invoke(sceneId, newIndex, reorderSceneOutputPort)
                    }
                }
                if (shouldShowNextTime != null) {
                    withContext(asyncContext) {
                        setDialogPreferences(DialogType.ReorderScene, shouldShowNextTime, setDialogPreferencesOutput)
                    }
                }
            } else {
                withContext(asyncContext) {
                    reorderScene.invoke(sceneId, newIndex, reorderSceneOutputPort)
                }
            }
        }
    }

}