package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.theme.usecases.changeCharacterDesire.ChangedCharacterDesire

interface ChangedCharacterDesireReceiver {

    suspend fun receiveChangedCharacterDesire(changedCharacterDesire: ChangedCharacterDesire)

}