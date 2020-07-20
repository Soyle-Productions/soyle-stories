package com.soyle.stories.characterarc.planNewCharacterArc

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.createTheme.CreateThemeNotifier
import com.soyle.stories.theme.usecases.ThemeItem
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import kotlin.coroutines.coroutineContext

class PlanNewCharacterArcNotifier(
    private val createThemeNotifier: CreateThemeNotifier
) : PlanNewCharacterArc.OutputPort, Notifier<PlanNewCharacterArc.OutputPort>() {

    override suspend fun themeNoted(response: CreatedTheme) {
        createThemeNotifier.themeCreated(response)
    }

    override suspend fun characterArcPlanned(response: CharacterArcItem) {
        notifyAll(coroutineContext) { it.characterArcPlanned(response) }
    }
}