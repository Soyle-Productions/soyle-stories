package com.soyle.stories.testutils

import com.soyle.stories.common.ThreadTransformer
import javafx.application.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.testfx.framework.junit5.ApplicationTest

class SyncThreadTransformer : ThreadTransformer, ApplicationTest() {
	override fun async(task: suspend CoroutineScope.() -> Unit) {
		runBlocking {
			task()
		}
	}

	override fun gui(update: suspend CoroutineScope.() -> Unit) {
		if (Platform.isFxApplicationThread()) {
			runBlocking {
				update()
			}
			return
		}
		interact {
			runBlocking {
				update()
			}
		}
	}
}