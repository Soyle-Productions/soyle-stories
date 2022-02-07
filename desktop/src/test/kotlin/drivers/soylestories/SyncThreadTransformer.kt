package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.soylestories.main
import javafx.application.Platform
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.*
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.CoroutineContext

class SyncThreadTransformer : ThreadTransformer {

	private val mainThread = Thread.getAllStackTraces().keys.single { it.name == "main" }
	private val uncaughtExceptionHandler = mainThread.uncaughtExceptionHandler

	override fun async(task: suspend CoroutineScope.() -> Unit): Job {
		return asyncScope.launch(CoroutineExceptionHandler { context, throwable ->
			uncaughtExceptionHandler.uncaughtException(mainThread, throwable)
		}) {
			task()
		}
	}

	override val asyncContext: CoroutineContext
		get() = asyncScope.coroutineContext + CoroutineExceptionHandler { coroutineContext, throwable ->
			uncaughtExceptionHandler.uncaughtException(mainThread, throwable)
		}

	override val guiContext: CoroutineContext
		get() = Dispatchers.JavaFx + CoroutineExceptionHandler { coroutineContext, throwable ->
			uncaughtExceptionHandler.uncaughtException(mainThread, throwable)
		}

	private val guiScope = CoroutineScope(guiContext)
	@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
	@OptIn(ExperimentalCoroutinesApi::class)
	override val asyncScope: CoroutineScope = TestCoroutineScope()
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