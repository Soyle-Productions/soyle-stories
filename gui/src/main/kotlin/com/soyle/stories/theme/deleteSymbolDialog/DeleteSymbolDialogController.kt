package com.soyle.stories.theme.deleteSymbolDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.deleteTheme.DeleteThemeController
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeController
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class DeleteSymbolDialogController(
    private val symbolId: String,
    private val threadTransformer: ThreadTransformer,
    private val getDialogPreferences: GetDialogPreferences,
    private val getDialogPreferencesOutputPort: GetDialogPreferences.OutputPort,
    private val removeSymbolFromThemeController: RemoveSymbolFromThemeController,
    private val setDialogPreferencesController: SetDialogPreferencesController
) : DeleteSymbolDialogViewListener {

    override fun getValidState() {
        threadTransformer.async {
            getDialogPreferences.invoke(
                DialogType.DeleteSymbol,
                getDialogPreferencesOutputPort
            )
        }
    }

    override fun deleteSymbol(showAgain: Boolean) {
        removeSymbolFromThemeController.removeSymbolFromTheme(symbolId)
        setDialogPreferencesController.setDialogPreferences("DeleteSymbol", showAgain)
    }

}