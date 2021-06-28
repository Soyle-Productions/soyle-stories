package com.soyle.stories.theme.changeCharacterPerspectiveProperty

import com.soyle.stories.usecase.theme.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue

class ChangeCharacterPerspectivePropertyValueOutput(
    private val characterPerspectivePropertyChangedReceiver: CharacterPerspectivePropertyChangedReceiver
) : ChangeCharacterPerspectivePropertyValue.OutputPort {

    override suspend fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
        characterPerspectivePropertyChangedReceiver.receiveCharacterPerspectivePropertyChanged(response)
    }

    override fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: Exception) {
        throw failure
    }
}