package com.soyle.stories.theme.updateThemeMetaData

import com.soyle.stories.theme.usecases.updateThemeMetaData.ChangeCentralConflict
import com.soyle.stories.theme.usecases.updateThemeMetaData.RenameTheme
import com.soyle.stories.theme.usecases.updateThemeMetaData.RenamedTheme

class UpdateThemeMetaDataOutput(
    private val renamedThemeReceiver: RenamedThemeReceiver,
    private val themeWithCentralConflictChangedReceiver: ThemeWithCentralConflictChangedReceiver
) : RenameTheme.OutputPort, ChangeCentralConflict.OutputPort {
    override suspend fun themeRenamed(response: RenamedTheme) {
        renamedThemeReceiver.receiveRenamedTheme(response)
    }

    override suspend fun centralConflictChanged(response: ChangeCentralConflict.ResponseModel) {
        themeWithCentralConflictChangedReceiver.receiveThemeWithCentralConflictChanged(response.themeWithChangedCentralConflict)
    }
}