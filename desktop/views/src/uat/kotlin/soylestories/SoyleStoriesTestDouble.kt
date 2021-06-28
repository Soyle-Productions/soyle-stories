package com.soyle.stories.soylestories

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.DI
import com.soyle.stories.di.configureDI
import com.soyle.stories.testutils.SyncThreadTransformer
import org.testfx.api.FxToolkit

class SoyleStoriesTestDouble {

	private val delegate = lazy { createApplication() }
	val application: SoyleStories by delegate

	private fun createApplication(): SoyleStories {
		setSystemProperties()
		FxToolkit.registerPrimaryStage()
		initializeDI()
		return FxToolkit.setupApplication(SoyleStories::class.java) as SoyleStories
	}

	private fun setSystemProperties() {
		System.setProperty("testfx.robot", "glass")
		System.setProperty("testfx.headless", "true")
		System.setProperty("prism.order", "sw")
		System.setProperty("prism.text", "t2k")
		System.setProperty("java.awt.headless", "true")
		System.setProperty("headless.geometry", "1600x1200-32")
	}

	private fun initializeDI() {
		configureDI()
		synchronizeBackgroundTasks()
	}

	private fun synchronizeBackgroundTasks() {
		DI.registerTypeFactory(ThreadTransformer::class, ApplicationScope::class) { SyncThreadTransformer() }
	}

	fun start() { application }
	fun isStarted() = delegate.isInitialized()

}