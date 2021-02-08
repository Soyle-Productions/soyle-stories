package com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemoved

interface CharacterArcSectionRemovedReceiver {

    suspend fun receiveCharacterArcSectionRemoved(event: CharacterArcSectionRemoved)

}