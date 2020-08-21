package com.soyle.stories.character.renameCharacter

import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.common.Notifier
import kotlin.coroutines.coroutineContext

class RenamedCharacterNotifier : RenamedCharacterReceiver, Notifier<RenamedCharacterReceiver>() {

    override suspend fun receiveRenamedCharacter(renamedCharacter: RenameCharacter.ResponseModel) {
        notifyAll { it.receiveRenamedCharacter(renamedCharacter) }
    }

}