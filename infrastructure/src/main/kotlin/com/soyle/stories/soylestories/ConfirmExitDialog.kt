package com.soyle.stories.soylestories

import javafx.application.Platform
import javafx.geometry.Pos
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/16/2020
 * Time: 10:38 AM
 */
class ConfirmExitDialog : View("Confirm Exit") {

    private val model = find<ApplicationModel>()

    override val root = vbox {
        label("Are you sure you want to exit Soyle Studio?")
        hbox(alignment = Pos.CENTER_RIGHT) {
            button("Exit") {
                isDefaultButton = true
                action {
                    Platform.exit()
                }
            }
            button("Cancel") {
                isCancelButton = true
                action {
                    model.closingProject.value = null
                }
            }
        }
    }

    init {
        model.closingProject.onChange {
            if (it == null) {
                close()
            }
            else {
                openModal()?.apply {
                    setOnCloseRequest {
                        model.closingProject.value = null
                    }
                    centerOnScreen()
                    isAlwaysOnTop = true
                }
            }
        }
    }

}
