package com.soyle.stories.soylestories

import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 3:19 PM
 */
class SplashScreen : Fragment() {

    private val model = find<ApplicationModel>()

    override val root = anchorpane {
        imageview(Image("com/soyle/stories/soylestories/splash.png")) {
            AnchorPane.setTopAnchor(this,0.0)
            AnchorPane.setLeftAnchor(this,0.0)
            AnchorPane.setRightAnchor(this,0.0)
            AnchorPane.setBottomAnchor(this,0.0)
            isPreserveRatio = true
        }
        label("Snapshot 20w15a") {
            alignment = Pos.CENTER_RIGHT
            anchorpaneConstraints {
                leftAnchor = 30.0
                topAnchor = 255.0
            }
            usePrefWidth = true
            maxWidth = Region.USE_PREF_SIZE
            prefWidth = 380.0
            style {
                textFill = Color.WHITE
                fontWeight = FontWeight.BOLD
                fontFamily = "Corbel"
                fontSize = 14.pt
            }
        }
        label(model.initializationMessage) {
            alignment = Pos.CENTER_RIGHT
            anchorpaneConstraints {
                leftAnchor = 30.0
                topAnchor = 281.0
            }
            usePrefWidth = true
            maxWidth = Region.USE_PREF_SIZE
            prefWidth = 380.0
            style {
                textFill = Color.WHITE
                fontFamily = "Corbel"
                fontSize = 10.pt
            }
        }
        progressbar {
            anchorpaneConstraints {
                leftAnchor = 30.0
                topAnchor = 304.0
            }
            //prefHeight = 8.0
            prefWidth = 380.0
            maxWidth = Region.USE_PREF_SIZE
            progressProperty().bind(model.initializationProgress)
        }
    }

    init {
        openModal(
            StageStyle.UNDECORATED, Modality.APPLICATION_MODAL,
            escapeClosesWindow = false, owner = null, block = false, resizable = false
        )?.apply {
            icons += SoyleStories.appIcon
            centerOnScreen()
            isAlwaysOnTop = true
            model.isSplashScreenVisible.onChange {
                close()
            }
        }
        Styles.run {  }
    }

    class Styles : Stylesheet() {

        companion object {
            init {
                importStylesheet(Styles::class)
            }
        }

        init {
            progressBar {
                track {
                    backgroundRadius += box(8.px)
                }
                bar {
                    accentColor = Color.GREY
                    backgroundRadius += box(6.px)
                    padding = box(6.px)
                    backgroundInsets += box(2.px, 2.px, 2.px, 2.px)
                }
            }
        }
    }

}