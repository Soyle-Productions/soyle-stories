package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class DeleteSceneDialogController(
  private val threadTransformer: ThreadTransformer,
  private val presenter: DeleteSceneDialogPresenter,
  private val deleteSceneController: DeleteSceneController,
  private val setDialogPreferencesController: SetDialogPreferencesController,
  private val getDialogPreferences: GetDialogPreferences,
  private val getDialogPreferencesOutputPort: GetDialogPreferences.OutputPort
) : DeleteSceneDialogViewListener {

	override fun getValidState(sceneItemViewModel: SceneItemViewModel) {
		presenter.displayDeleteSceneDialog(sceneItemViewModel)
		threadTransformer.async {
			getDialogPreferences.invoke(
			  DialogType.DeleteScene,
			  getDialogPreferencesOutputPort
			)
		}
	}

	override fun viewRamifications(sceneId: String, showNextTime: Boolean) {
		setDialogPreferencesController.setDialogPreferences(DialogType.DeleteScene.name, showNextTime)
	}

	override fun deleteScene(sceneId: String, showNextTime: Boolean) {
		deleteSceneController.deleteScene(sceneId)
		setDialogPreferencesController.setDialogPreferences(DialogType.DeleteScene.name, showNextTime)
	}
}