package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.drivers.robot
import javafx.application.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

class SyncThreadTransformer : ThreadTransformer {
	override fun async(task: suspend CoroutineScope.() -> Unit): Job {
		val job = Job()
		runBlocking {
			task()
		}
		job.complete()
		return job
	}

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