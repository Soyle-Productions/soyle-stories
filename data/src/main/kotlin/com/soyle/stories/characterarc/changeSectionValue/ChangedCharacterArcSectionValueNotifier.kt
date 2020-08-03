package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import kotlin.coroutines.coroutineContext

class ChangedCharacterArcSectionValueNotifier : ChangedCharacterArcSectionValueReceiver, Notifier<ChangedCharacterArcSectionValueReceiver>() {

    override suspend fun receiveChangedCharacterArcSectionValue(changedCharacterArcSectionValue: ChangedCharacterArcSectionValue) {
        println("${changedCharacterArcSectionValue.type.name} changed to ${changedCharacterArcSectionValue.newValue}")
        notifyAll(coroutineContext) { it.receiveChangedCharacterArcSectionValue(changedCharacterArcSectionValue) }
    }
}