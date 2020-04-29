package com.soyle.stories.soylestories

import com.soyle.stories.di.DI
import com.soyle.stories.di.characterarc.CharacterArcModule
import com.soyle.stories.di.layout.LayoutModule
import com.soyle.stories.di.location.LocationModule
import com.soyle.stories.di.modules.ApplicationModule
import com.soyle.stories.di.modules.DataModule
import com.soyle.stories.di.project.ProjectModule
import com.soyle.stories.gui.ThreadTransformer
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
	}

	private fun initializeDI() {
		ApplicationModule
		DataModule
		ProjectModule
		LayoutModule
		LocationModule
		CharacterArcModule
		synchronizeBackgroundTasks()
	}

	private fun synchronizeBackgroundTasks() {
		DI.registerTypeFactory(ThreadTransformer::class, ApplicationScope::class) { SyncThreadTransformer() }
	}

	fun start() { application }
	fun isStarted() = delegate.isInitialized()

}