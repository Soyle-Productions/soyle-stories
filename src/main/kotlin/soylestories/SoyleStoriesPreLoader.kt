package com.soyle.stories.desktop.config.soylestories

import javafx.application.Preloader
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*
import java.util.*

class SoyleStoriesPreLoader : Preloader() {

    private val initializationMessage = SimpleStringProperty()
    private val initializationProgress = SimpleDoubleProperty()

    private val applicationProperties: Properties = Properties()

    private lateinit var stage: Stage

    override fun start(primaryStage: Stage?) {
        stage = primaryStage ?: return
        SoyleStoriesPreLoader::class.java.classLoader.getResourceAsStream("soylestories/SoyleStoriesPreLoader.properties")?.let {
            applicationProperties.load(it)
        } ?: println("no properties file found")
        primaryStage.initStyle(StageStyle.UNDECORATED)
        primaryStage.scene = createPreloaderScene()
        primaryStage.show()
    }

    private fun createPreloaderScene(): Scene {
        val root = AnchorPane().apply {
            imageview(Image("soylestories/splash.png")) {
                AnchorPane.setTopAnchor(this, 0.0)
                AnchorPane.setLeftAnchor(this, 0.0)
                AnchorPane.setRightAnchor(this, 0.0)
                AnchorPane.setBottomAnchor(this, 0.0)
                isPreserveRatio = true
            }
            label(applicationProperties.getProperty("application.version") ?: "DEVELOPMENT") {
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
            label(initializationMessage) {
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
                progressProperty().bind(initializationProgress)
            }
        }
        return Scene(root)
    }

    override fun handleProgressNotification(info: ProgressNotification?) {
        info?.let {
            initializationProgress.set(info.progress)
        }
    }

    override fun handleStateChangeNotification(info: StateChangeNotification?) {
        if (info?.type == StateChangeNotification.Type.BEFORE_START) {
            stage.hide()
        }
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