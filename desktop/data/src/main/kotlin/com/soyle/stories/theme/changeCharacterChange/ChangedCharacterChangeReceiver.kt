package com.soyle.stories.theme.changeCharacterChange

import com.soyle.stories.theme.usecases.changeCharacterChange.ChangedCharacterChange

interface ChangedCharacterChangeReceiver {

    suspend fun receiveChangedCharacterChange(changedCharacterChange: ChangedCharacterChange)

}