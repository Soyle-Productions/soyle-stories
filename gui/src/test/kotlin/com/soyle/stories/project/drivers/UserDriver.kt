package com.soyle.stories.project.drivers

import com.soyle.stories.project.UserInterfaceInputState

class UserDriver(
    private val uiState: UserInterfaceInputState
) {

    fun selectDirectory(directory: String) {
        uiState.fields[UserInterfaceInputState.Field.Directory] = directory
    }

    fun enterTextIntoField(text: String, field: UserInterfaceInputState.Field) {
        uiState.fields[field] = text
    }

}