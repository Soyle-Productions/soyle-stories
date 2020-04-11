package com.soyle.stories.soylestories.confirmExitDialog

import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.modules.ApplicationComponent
import com.soyle.stories.soylestories.ApplicationModel
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/16/2020
 * Time: 10:38 AM
 */
class ConfirmExitDialog : Fragment() {

    private val model = find<ConfirmExitDialogModel>()
    private val viewListener: ConfirmExitDialogViewListener = find<ApplicationComponent>().confirmExitDialogViewListener

    override val root = region()

    init {
        model.closingProject.onChange {
            if (it != null) {
                alert(
                  type = Alert.AlertType.CONFIRMATION,
                  title = model.title.value,
                  header = model.header.value,
                  owner = currentWindow,
                  buttons = *arrayOf(ButtonType(model.exitButton.value, ButtonBar.ButtonData.YES), ButtonType(model.cancelButton.value, ButtonBar.ButtonData.CANCEL_CLOSE))
                ) {
                    when (it.buttonData) {
                        ButtonBar.ButtonData.YES -> Platform.exit()
                        ButtonBar.ButtonData.CANCEL_CLOSE -> {
                            model.closingProject.value = null
                        }
                        else -> {}
                    }
                }
            }
        }
        model.isInvalid.onChangeWithCurrent {
            if (it != false) viewListener.initializeConfirmExitDialog()
        }
    }

}
