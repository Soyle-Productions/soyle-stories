package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.drivers.robot
import javafx.application.Platform
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.*
import kotlinx.coroutines.test.TestCoroutineContext
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.CoroutineContext

class SyncThreadTransformer : ThreadTransformer {

	override fun async(task: suspend CoroutineScope.() -> Unit): Job {
		return asyncScope.launch {
			task()
		}
	}

	override val asyncContext: CoroutineContext
		get() = asyncScope.coroutineContext

	override val guiContext: CoroutineContext
		get() = guiScope.coroutineContext

	private val guiScope = TestCoroutineScope() + Dispatchers.JavaFx
	private val asyncScope = TestCoroutineScope()
	override fun isGuiThread(): Boolean = Platform.isFxApplicationThread()

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