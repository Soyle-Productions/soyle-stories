package com.soyle.stories.writer.getDialogPreference

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

interface GetDialogPreferenceController {

    fun getPreferenceForDialog(type: DialogType, outputPort: GetDialogPreferences.OutputPort)

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            getDialogPreferences: GetDialogPreferences
        ): GetDialogPreferenceController = object : GetDialogPreferenceController {
            override fun getPreferenceForDialog(type: DialogType, outputPort: GetDialogPreferences.OutputPort) {
                threadTransformer.async {
                    getDialogPreferences.invoke(type, outputPort)
                }
            }
        }
    }

}