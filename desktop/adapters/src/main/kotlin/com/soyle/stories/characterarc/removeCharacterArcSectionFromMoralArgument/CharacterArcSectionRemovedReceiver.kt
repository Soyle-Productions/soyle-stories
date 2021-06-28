package com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.usecase.character.arc.section.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemoved

interface CharacterArcSectionRemovedReceiver {

    suspend fun receiveCharacterArcSectionRemoved(event: CharacterArcSectionRemoved)

}