package com.soyle.stories.soylestories

import com.soyle.stories.di.modules.ApplicationComponent
import com.soyle.stories.project.startProjectDialog.startProjectDialog
import com.soyle.stories.soylestories.Styles.Companion.welcomeButtonGraphic
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 3:17 PM
 */
class WelcomeScreen : View("Welcome to Soyle Stories") {

    private val projectListViewListener = find<ApplicationComponent>().projectListViewListener

    override val root: Parent = vbox(alignment = Pos.CENTER, spacing = 40) {
        paddingAll = 20.0
        isFillWidth = false
        minWidth = 400.0
        setStyle("-fx-background-color: linear-gradient(from 0% 15% to 100% 100%, #3A518E, #862F89)")/*
        style {
            backgroundColor = MultiValue(arrayOf(Color.web("#3A518E"), Color.web("#862F89")))
        }*/
        imageview(SoyleStories.logo) {
            fitHeight = 200.0
            isPreserveRatio = true
        }
        label("Soyle Stories") {
            style {
                textFill = Color.WHITE
                fontSize = 2.em
            }
        }
        vbox(alignment = Pos.CENTER_LEFT, spacing = 10) {
            button("Create New Project") {
                graphic = MaterialIconView(MaterialIcon.CREATE_NEW_FOLDER, "14").apply {
                    addClass(welcomeButtonGraphic)
                }
                addClass(Styles.welcomeButton)
                action {
                    startProjectDialog(currentStage)
                }
            }
            button("Open Project") {
                graphic = MaterialIconView(MaterialIcon.FOLDER_OPEN, "14").apply {
                    addClass(welcomeButtonGraphic)
                }
                addClass(Styles.welcomeButton)
                action {
                    chooseFile("Choose Project", arrayOf(FileChooser.ExtensionFilter("Storyline File", "*.stry")), owner = currentStage)
                }
            }
        }
    }

    init {
        val model = find<ApplicationModel>()
        model.isWelcomeScreenVisible.onChange {
            if (it == true) {
                primaryStage.show()
            } else {
                primaryStage.hide()
            }
        }
    }

}

class Styles : Stylesheet() {
    companion object {
        val welcomeButton by cssclass()
        val welcomeButtonGraphic by cssclass()

        init {
            importStylesheet(Styles::class)
        }
    }

    init {
        welcomeButton {
            backgroundColor += Color.TRANSPARENT
            textFill = Color.web("#FFC9A3")
            welcomeButtonGraphic {
                fill = Color.web("#FFC9A3")
            }
            graphicTextGap = 7.px
            and(focused) {
                borderWidth += box(1.0.px)
                borderInsets += box((-1.0).px)
                borderColor += box(Color.web("#FFC9A3"))
            }
            and(hover) {
                textFill = Color.WHITE
                cursor = Cursor.HAND
                welcomeButtonGraphic {
                    fill = Color.WHITE
                }
            }
        }
    }
}