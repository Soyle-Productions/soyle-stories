package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.theme.usecases.changeThemeDetails.ChangeCentralConflict
import com.soyle.stories.theme.usecases.changeThemeDetails.ChangeCentralMoralQuestion
import com.soyle.stories.theme.usecases.changeThemeDetails.RenameTheme
import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme

class ChangeThemeDetailsOutput(
    private val renamedThemeReceiver: RenamedThemeReceiver,
    private val themeWithCentralConflictChangedReceiver: ThemeWithCentralConflictChangedReceiver,
    private val changedCentralMoralQuestionReceiver: ChangedCentralMoralQuestionReceiver
) : RenameTheme.OutputPort, ChangeCentralConflict.OutputPort, ChangeCentralMoralQuestion.OutputPort {
    override suspend fun themeRenamed(response: RenamedTheme) {
        renamedThemeReceiver.receiveRenamedTheme(response)
    }

    override suspend fun centralConflictChanged(response: ChangeCentralConflict.ResponseModel) {
        themeWithCentralConflictChangedReceiver.receiveThemeWithCentralConflictChanged(response.changedCentralConflict)
    }

    override suspend fun centralMoralQuestionChanged(response: ChangeCentralMoralQuestion.ResponseModel) {
        changedCentralMoralQuestionReceiver.receiveChangedCentralMoralQuestion(response.changedCentralMoralQuestion)
    }
}