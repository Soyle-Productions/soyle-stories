package com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.characterarc.usecases.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemoved

interface CharacterArcSectionRemovedReceiver {

    suspend fun receiveCharacterArcSectionRemoved(event: CharacterArcSectionRemoved)

}