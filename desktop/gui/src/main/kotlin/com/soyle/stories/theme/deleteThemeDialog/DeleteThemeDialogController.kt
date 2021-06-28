package com.soyle.stories.theme.deleteThemeDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.deleteTheme.DeleteThemeController
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class DeleteThemeDialogController(
    private val themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val getDialogPreferences: GetDialogPreferences,
    private val getDialogPreferencesOutputPort: GetDialogPreferences.OutputPort,
    private val deleteThemeController: DeleteThemeController,
    private val setDialogPreferencesController: SetDialogPreferencesController
) : DeleteThemeDialogViewListener {

    override fun getValidState() {
        threadTransformer.async {
            getDialogPreferences.invoke(
                DialogType.DeleteTheme,
                getDialogPreferencesOutputPort
            )
        }
    }

    override fun deleteTheme(showAgain: Boolean) {
        deleteThemeController.deleteTheme(themeId)
        setDialogPreferencesController.setDialogPreferences("DeleteTheme", showAgain)
    }

}