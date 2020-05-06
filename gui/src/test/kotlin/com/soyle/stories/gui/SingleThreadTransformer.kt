package com.soyle.stories.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

object SingleThreadTransformer : ThreadTransformer {
	override fun async(task: suspend CoroutineScope.() -> Unit) {
		runBlocking { task() }
	}

	override fun gui(update: suspend CoroutineScope.() -> Unit) {
		runBlocking { update() }
	}
}