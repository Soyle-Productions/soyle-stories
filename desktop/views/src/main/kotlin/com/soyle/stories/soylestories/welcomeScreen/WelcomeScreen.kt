package com.soyle.stories.soylestories.welcomeScreen

import com.soyle.stories.common.components.text.ApplicationTitle.Companion.applicationTitle
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.resolve
import com.soyle.stories.project.startProjectDialog.startProjectDialog
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStories
import com.soyle.stories.soylestories.Styles.Companion.logo
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import tornadofx.*

class WelcomeScreen : View() {

    override val scope: ApplicationScope = super.scope as ApplicationScope

    private val welcomeScreenViewListener = resolve<WelcomeScreenViewListener>()
    private val model = find<WelcomeScreenModel>()

    override val root: Parent = vbox(alignment = Pos.CENTER, spacing = 40) {
        paddingAll = 20.0
        isFillWidth = false
        minWidth = 400.0
        setStyle("-fx-background-color: linear-gradient(from 0% 15% to 100% 100%, #3A518E, #862F89)")
        imageview(logo) {
            fitHeight = 200.0
            isPreserveRatio = true
        }
        applicationTitle {
            textProperty().bind(model.applicationName)
            style {
                textFill = Color.WHITE
            }
        }
        vbox(alignment = Pos.CENTER_LEFT, spacing = 10) {
            button(model.createNewProjectButton) {
                id = "createNewProject"
                graphic = MaterialIconView(MaterialIcon.CREATE_NEW_FOLDER, "14").apply {
                    addClass(WelcomeScreenStyles.welcomeButtonGraphic)
                }
                addClass(WelcomeScreenStyles.welcomeButton)
                action {
                    startProjectDialog(scope, currentStage)
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
        model.isOpen.onChangeWithCurrent {
            if (it == true) {
                primaryStage.titleProperty().bind(titleProperty)
                primaryStage.scene.root = root
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

