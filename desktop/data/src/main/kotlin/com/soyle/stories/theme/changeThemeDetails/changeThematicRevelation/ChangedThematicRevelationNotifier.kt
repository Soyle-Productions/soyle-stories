package com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangedThematicRevelationReceiver
import com.soyle.stories.theme.usecases.changeThemeDetails.ChangedThematicRevelation

class ChangedThematicRevelationNotifier : ChangedThematicRevelationReceiver, Notifier<ChangedThematicRevelationReceiver>() {

    override suspend fun receiveChangedThematicRevelation(event: ChangedThematicRevelation) {
        notifyAll { it.receiveChangedThematicRevelation(event) }
    }

}