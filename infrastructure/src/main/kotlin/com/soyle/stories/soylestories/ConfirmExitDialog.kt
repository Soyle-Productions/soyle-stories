package com.soyle.stories.soylestories

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/16/2020
 * Time: 10:38 AM
 */
class ConfirmExitDialog : Fragment() {

    private val model = find<ApplicationModel>()

    override val root = region()

    init {
        model.closingProject.onChange {
            if (it != null) {
                alert(
                  type = Alert.AlertType.CONFIRMATION,
                  title = "Confirm Exit",
                  header = "Exit Soyle Stories?",
                  owner = currentWindow,
                  buttons = *arrayOf(ButtonType("Exit", ButtonBar.ButtonData.YES), ButtonType.CANCEL)
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
    }

}
