package com.soyle.stories.theme.changeCharacterPerspectiveProperty

import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue

class ChangeCharacterPerspectivePropertyValueOutput(
    private val characterPerspectivePropertyChangedReceiver: CharacterPerspectivePropertyChangedReceiver
) : ChangeCharacterPerspectivePropertyValue.OutputPort {

    override suspend fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
        characterPerspectivePropertyChangedReceiver.receiveCharacterPerspectivePropertyChanged(response)
    }

    override fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: ThemeException) {
        throw failure
    }
}