package com.soyle.stories.soylestories.welcomeScreen

import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.modules.ApplicationComponent
import com.soyle.stories.project.startProjectDialog.startProjectDialog
import com.soyle.stories.soylestories.SoyleStories
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import tornadofx.*

class WelcomeScreen : View() {

    private val welcomeScreenViewListener = find<ApplicationComponent>().welcomeScreenViewListener
    private val model = find<WelcomeScreenModel>()

    override val root: Parent = vbox(alignment = Pos.CENTER, spacing = 40) {
        paddingAll = 20.0
        isFillWidth = false
        minWidth = 400.0
        setStyle("-fx-background-color: linear-gradient(from 0% 15% to 100% 100%, #3A518E, #862F89)")
        imageview(SoyleStories.logo) {
            fitHeight = 200.0
            isPreserveRatio = true
        }
        label(model.applicationName) {
            style {
                textFill = Color.WHITE
                fontSize = 2.em
            }
        }
        vbox(alignment = Pos.CENTER_LEFT, spacing = 10) {
            button(model.createNewProjectButton) {
                graphic = MaterialIconView(MaterialIcon.CREATE_NEW_FOLDER, "14").apply {
                    addClass(WelcomeScreenStyles.welcomeButtonGraphic)
                }
                addClass(WelcomeScreenStyles.welcomeButton)
                action {
                    startProjectDialog(currentStage)
                }
            }
            button(model.openProjectButton) {
                graphic = MaterialIconView(MaterialIcon.FOLDER_OPEN, "14").apply {
                    addClass(WelcomeScreenStyles.welcomeButtonGraphic)
                }
                addClass(WelcomeScreenStyles.welcomeButton)
                action {
                    chooseFile("Choose Project", arrayOf(FileChooser.ExtensionFilter("Storyline File", "*.stry")), owner = currentStage)
                }
            }
        }
    }

    init {
        titleProperty.bind(model.title)
        model.isOpen.onChange {
            if (it == true) {
                primaryStage.show()
            } else {
                primaryStage.hide()
            }
        }
        model.isInvalid.onChangeWithCurrent {
            if (it != false) welcomeScreenViewListener.initializeWelcomeScreen()
        }
    }

}

