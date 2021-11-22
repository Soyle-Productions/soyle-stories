package com.soyle.stories.scene.reorder

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.PromptChoice
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream

class ReorderSceneControllerImpl(
    threadTransformer: ThreadTransformer,

    private val dialogPreferences: suspend (DialogType) -> DialogPreference,

    private val getPromptForScene: (Scene.Id) -> ReorderScenePrompt,
    private val getReportForScene: (Scene.Id) -> ReorderSceneRamificationsReport,

    private val sceneRepository: SceneRepository,

    private val reorderScene: ReorderScene,
    private val reorderSceneOutputPort: ReorderScene.OutputPort,
    private val setDialogPreferences: SetDialogPreferences,
    private val setDialogPreferencesOutput: SetDialogPreferences.OutputPort,

    private val getPotentialChangesFromReorderingScene: GetPotentialChangesFromReorderingScene
) : ReorderSceneController, CoroutineScope by CoroutineScope(threadTransformer.guiContext) {

    private val asyncContext = threadTransformer.asyncContext

    override fun reorderScene(sceneId: Scene.Id, newIndex: Int): Job {
        return launch {
            if (dialogPreferences(DialogType.ReorderScene).shouldShow) {
                showPrompt(sceneId, newIndex)
            } else {
                withContext(asyncContext) {
                    reorderScene.invoke(sceneId, newIndex, reorderSceneOutputPort)
                }
            }
        }

    }

    private suspend fun showPrompt(
        sceneId: Scene.Id,
        newIndex: Int
    ) {
        val scene = withContext(asyncContext) {
            sceneRepository.getSceneOrError(sceneId.uuid)
        }
        val prompt = getPromptForScene(sceneId)
        prompt.use {
            val confirmation = prompt.requestConfirmation(scene.name.value) ?: return
            when (confirmation.choice) {
                PromptChoice.Confirm -> reorderSceneConfirmed(sceneId, newIndex)
                PromptChoice.ShowRamifications -> showRamifications(sceneId, newIndex)
            }
            updatePreferences(confirmation.showAgain)
        }
    }

    private suspend fun reorderSceneConfirmed(sceneId: Scene.Id, newIndex: Int) {
        withContext(asyncContext) {
            reorderScene.invoke(sceneId, newIndex, reorderSceneOutputPort)
        }
    }

    private suspend fun showRamifications(sceneId: Scene.Id, newIndex: Int) {
        val report = getReportForScene(sceneId)
        withContext(asyncContext) {
            getPotentialChangesFromReorderingScene.invoke(
                sceneId.uuid,
                newIndex,
                report
            )
        }
        report.requestContinuation() ?: return
        reorderSceneConfirmed(sceneId, newIndex)
    }

    private suspend fun updatePreferences(showAgain: Boolean) {
        withContext(asyncContext) {
            setDialogPreferences(
                DialogType.ReorderScene,
                showAgain,
                setDialogPreferencesOutput
            )
        }
    }

}