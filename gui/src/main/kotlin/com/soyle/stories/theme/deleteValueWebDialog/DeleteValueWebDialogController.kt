package com.soyle.stories.theme.deleteValueWebDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeController
import com.soyle.stories.theme.removeValueWebFromTheme.RemoveValueWebFromThemeController
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class DeleteValueWebDialogController(
    private val valueWebId: String,
    private val threadTransformer: ThreadTransformer,
    private val getDialogPreferences: GetDialogPreferences,
    private val getDialogPreferencesOutputPort: GetDialogPreferences.OutputPort,
    private val removeValueWebFromThemeController: RemoveValueWebFromThemeController,
    private val setDialogPreferencesController: SetDialogPreferencesController
) : DeleteValueWebDialogViewListener {

    override fun getValidState() {
        threadTransformer.async {
            getDialogPreferences.invoke(
                DialogType.DeleteSymbol,
                getDialogPreferencesOutputPort
            )
        }
    }

    override fun deleteValueWeb(showAgain: Boolean) {
        removeValueWebFromThemeController.removeValueWeb(valueWebId)
        setDialogPreferencesController.setDialogPreferences("DeleteValueWeb", showAgain)
    }

}