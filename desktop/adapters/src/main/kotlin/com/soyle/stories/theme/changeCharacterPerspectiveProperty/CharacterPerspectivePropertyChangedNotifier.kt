package com.soyle.stories.theme.changeCharacterPerspectiveProperty

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import kotlin.coroutines.coroutineContext

class CharacterPerspectivePropertyChangedNotifier : CharacterPerspectivePropertyChangedReceiver, Notifier<CharacterPerspectivePropertyChangedReceiver>() {

    override suspend fun receiveCharacterPerspectivePropertyChanged(propertyChanged: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
        println(propertyChanged.property.name + " changed to ${propertyChanged.newValue}")
        notifyAll { it.receiveCharacterPerspectivePropertyChanged(propertyChanged) }
    }
}