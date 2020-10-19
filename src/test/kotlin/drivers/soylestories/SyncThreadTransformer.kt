package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.drivers.robot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

class SyncThreadTransformer : ThreadTransformer {
	override fun async(task: suspend CoroutineScope.() -> Unit) {
		runBlocking {
			task()
		}
	}

	override fun gui(update: suspend CoroutineScope.() -> Unit) {
		robot.interact {
			runBlocking {
				update()
			}
		}
	}
}