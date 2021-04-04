package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue

class ChangedCharacterArcSectionValueNotifier : ChangedCharacterArcSectionValueReceiver, Notifier<ChangedCharacterArcSectionValueReceiver>() {

    override suspend fun receiveChangedCharacterArcSectionValue(changedCharacterArcSectionValue: ChangedCharacterArcSectionValue) {
        notifyAll { it.receiveChangedCharacterArcSectionValue(changedCharacterArcSectionValue) }
    }
}