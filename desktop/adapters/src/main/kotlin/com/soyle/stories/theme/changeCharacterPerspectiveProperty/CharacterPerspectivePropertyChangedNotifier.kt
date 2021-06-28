package com.soyle.stories.theme.changeCharacterPerspectiveProperty

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue

class CharacterPerspectivePropertyChangedNotifier : CharacterPerspectivePropertyChangedReceiver, Notifier<CharacterPerspectivePropertyChangedReceiver>() {

    override suspend fun receiveCharacterPerspectivePropertyChanged(propertyChanged: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
        println(propertyChanged.property.name + " changed to ${propertyChanged.newValue}")
        notifyAll { it.receiveCharacterPerspectivePropertyChanged(propertyChanged) }
    }
}