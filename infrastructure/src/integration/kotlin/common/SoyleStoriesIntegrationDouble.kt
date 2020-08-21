package com.soyle.stories.common

import com.soyle.stories.di.DI
import com.soyle.stories.di.configureDI
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.project.WorkbenchDouble
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.ApplicationModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.testfx.api.FxToolkit
import tornadofx.bind

class SoyleStoriesIntegrationDouble {

    private val delegate = lazy { createApplication() }
    val scope: ApplicationScope by delegate

    private val workbenches = FXCollections.observableArrayList<WorkbenchDouble>()

    private fun createApplication(): ApplicationScope {
        setSystemProperties()
        FxToolkit.registerPrimaryStage()
        initializeDI()
        val scope = ApplicationScope()

        workbenches.bind(scope.projectScopesProperty) {
            it.get()
        }

        return scope
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

    fun start() {
        delegate.value
    }

    fun isStarted() = delegate.isInitialized()
}