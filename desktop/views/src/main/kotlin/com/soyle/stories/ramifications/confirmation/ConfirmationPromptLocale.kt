package com.soyle.stories.ramifications.confirmation

import javafx.beans.value.ObservableValue

interface ConfirmationPromptLocale {

    val doNotShowDialogAgain: ObservableValue<String>

    val confirm: ObservableValue<String>
    val ramifications: ObservableValue<String>
    val cancel: ObservableValue<String>

}