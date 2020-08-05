package com.soyle.stories.character.renameCharacter

import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter

interface RenamedCharacterReceiver {

    suspend fun receiveRenamedCharacter(renamedCharacter: RenameCharacter.ResponseModel)

}