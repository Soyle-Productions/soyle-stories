package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.drivers.robot
import javafx.application.Platform
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.*

class SyncThreadTransformer : ThreadTransformer {

	override fun async(task: suspend CoroutineScope.() -> Unit): Job {
		return if (Platform.isFxApplicationThread()) {
			guiScope.launch { task() }
		} else runBlocking {
			launch {
				task()
			}
		}
	}

	private val guiScope = CoroutineScope(Dispatchers.JavaFx)

	override fun gui(update: suspend CoroutineScope.() -> Unit) {
		if (Platform.isFxApplicationThread()) {
			runBlocking {
				update()
			}
		} else {
			robot.interact {
				gui(update)
			}
		}
	}
}