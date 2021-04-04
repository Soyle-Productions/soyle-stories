package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue

interface ChangedCharacterArcSectionValueReceiver {

    suspend fun receiveChangedCharacterArcSectionValue(changedCharacterArcSectionValue: ChangedCharacterArcSectionValue)

}