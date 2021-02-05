package com.soyle.stories.characterarc.planNewCharacterArc

import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.createTheme.CreatedThemeReceiver

class PlanNewCharacterArcOutput(
    private val createdCharacterArcReceiver: CreatedCharacterArcReceiver,
    private val createdThemeReceiver: CreatedThemeReceiver
) : PlanNewCharacterArc.OutputPort {

    override suspend fun characterArcPlanned(response: PlanNewCharacterArc.ResponseModel) {
        createdThemeReceiver.receiveCreatedTheme(response.createdTheme)
        createdCharacterArcReceiver.receiveCreatedCharacterArc(response.createdCharacterArc)
    }
}