package com.soyle.stories.theme.changeCharacterChange

import com.soyle.stories.usecase.theme.changeCharacterChange.ChangedCharacterChange

interface ChangedCharacterChangeReceiver {

    suspend fun receiveChangedCharacterChange(changedCharacterChange: ChangedCharacterChange)

}