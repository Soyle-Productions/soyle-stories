package com.soyle.stories.project

import com.soyle.stories.common.onChangeUntil
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:11 PM
 */
class ProjectLoadingDialog : View() {

    private val model = find<WorkBenchModel>()

    override val root: Parent = hbox(spacing = 10.0) {
        minWidth = 400.0
        vbox(spacing = 2.0) {
            hgrow = Priority.ALWAYS
            label(model.loadingMessage) {
                fitToParentWidth()
            }
            progressbar(model.loadingProgress) {
                fitToParentWidth()
                progressProperty().onChange {
                    if (it >= WorkBenchModel.MAX_LOADING_VALUE) {
                        close()
                    }
                }
            }
        }
        button("Cancel") {
            isMnemonicParsing = false
        }

        paddingAll = 10.0
    }

    init {
        openModal(
            StageStyle.UTILITY,
            Modality.WINDOW_MODAL,
            escapeClosesWindow = false,
            owner = null,
            block = false,
            resizable = false
        )?.apply {
            centerOnScreen()
            isAlwaysOnTop = true
            model.isValidLayout.onChangeUntil(this.showingProperty().not()) {
                if (it == true) close()
            }
        }
    }

}