package com.soyle.stories.theme.changeCharacterPerspectiveProperty

import com.soyle.stories.usecase.theme.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue

interface CharacterPerspectivePropertyChangedReceiver {

    suspend fun receiveCharacterPerspectivePropertyChanged(propertyChanged: ChangeCharacterPerspectivePropertyValue.ResponseModel)

}