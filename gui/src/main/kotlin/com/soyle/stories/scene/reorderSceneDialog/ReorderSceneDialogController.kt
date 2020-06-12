package com.soyle.stories.scene.reorderSceneDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.reorderScene.ReorderSceneController
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class ReorderSceneDialogController(
  private val threadTransformer: ThreadTransformer,
  private val presenter: ReorderSceneDialogPresenter,
  private val reorderSceneController: ReorderSceneController,
  private val getDialogPreferences: GetDialogPreferences,
  private val setDialogPreferencesController: SetDialogPreferencesController
) : ReorderSceneDialogViewListener {

	override fun getValidState(sceneId: String, sceneName: String, index: Int) {
		presenter.displayReorderSceneDialog(sceneName)
		threadTransformer.async {
			getDialogPreferences.invoke(
			  DialogType.ReorderScene,
			  presenter
			)
		}
	}

	override fun reorderScene(sceneId: String, index: Int, showNextTime: Boolean) {
		reorderSceneController.reorderScene(sceneId, index)
		setDialogPreferencesController.setDialogPreferences(DialogType.ReorderScene.name, showNextTime)
	}

}