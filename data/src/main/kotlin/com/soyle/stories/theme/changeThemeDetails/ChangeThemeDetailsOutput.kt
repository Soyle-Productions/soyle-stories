package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.theme.changeThemeDetails.changeCentralConflict.CentralConflictChangedReceiver
import com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion.ChangedCentralMoralQuestionReceiver
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangedThematicRevelationReceiver
import com.soyle.stories.theme.changeThemeDetails.changeThemeLine.ChangedThemeLineReceiver
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeReceiver
import com.soyle.stories.theme.usecases.changeThemeDetails.*

class ChangeThemeDetailsOutput(
    private val renamedThemeReceiver: RenamedThemeReceiver,
    private val centralConflictChangedReceiver: CentralConflictChangedReceiver,
    private val changedCentralMoralQuestionReceiver: ChangedCentralMoralQuestionReceiver,
    private val changedThemeLineReceiver: ChangedThemeLineReceiver,
    private val changedThematicRevelationReceiver: ChangedThematicRevelationReceiver
) : RenameTheme.OutputPort, ChangeCentralConflict.OutputPort, ChangeCentralMoralQuestion.OutputPort,
    ChangeThemeLine.OutputPort, ChangeThematicRevelation.OutputPort {
    override suspend fun themeRenamed(response: RenamedTheme) {
        renamedThemeReceiver.receiveRenamedTheme(response)
    }

    override suspend fun centralConflictChanged(response: ChangeCentralConflict.ResponseModel) {
        centralConflictChangedReceiver.receiveThemeWithCentralConflictChanged(response.changedCentralConflict)
    }

    override suspend fun centralMoralQuestionChanged(response: ChangeCentralMoralQuestion.ResponseModel) {
        changedCentralMoralQuestionReceiver.receiveChangedCentralMoralQuestion(response.changedCentralMoralQuestion)
    }

    override suspend fun themeLineChanged(response: ChangeThemeLine.ResponseModel) {
        changedThemeLineReceiver.receiveChangedThemeLine(response.changedThemeLine)
    }

    override suspend fun thematicRevelationChanged(response: ChangeThematicRevelation.ResponseModel) {
        changedThematicRevelationReceiver.receiveChangedThematicRevelation(response.changedThematicRevelation)
    }
}