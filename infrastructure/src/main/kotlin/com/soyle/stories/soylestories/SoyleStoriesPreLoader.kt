package com.soyle.stories.soylestories

import javafx.application.Preloader
import javafx.stage.Stage

class SoyleStoriesPreLoader : Preloader() {
	override fun start(primaryStage: Stage) {
		primaryStage.show()
	}

}