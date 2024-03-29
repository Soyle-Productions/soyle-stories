package com.soyle.stories.soylestories

import com.soyle.stories.common.async
import com.soyle.stories.di.DI
import com.soyle.stories.di.configureDI
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.Styles.Companion.appIcon
import com.soyle.stories.soylestories.confirmExitDialog.ConfirmExitDialog
import com.soyle.stories.soylestories.welcomeScreen.WelcomeScreen
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 2:50 PM
 */
class SoyleStories : App(stylesheet = Styles::class) {

    companion object {
        lateinit var initialization: () -> Unit
    }

    private val appScope = ApplicationScope()
    override var scope: Scope = appScope

    private val applicationModel = find<ApplicationModel>(appScope)
    private val projectListViewListener: ProjectListViewListener by DI.resolveLater(appScope)

    val projectViews: ObservableList<WorkBench> = FXCollections.observableArrayList()
    private var projectScopeListener: SetConversionListener<ProjectScope, WorkBench>? = null

    override fun shouldShowPrimaryStage(): Boolean = false

    override fun init() {
        initialization()
        runBlocking {
            projectListViewListener.startApplicationWithParameters(parameters.raw)
        }
    }

    override fun start(stage: Stage) {
        super.start(stage)

        stage.icons += appIcon

        find<WelcomeScreen>(appScope)
        find<FailedProjectsDialog>(appScope)
        find<ConfirmExitDialog>(appScope)
        find<OpenProjectOptionDialog>(appScope)

        projectScopeListener = projectViews.bind(appScope.projectScopesProperty) {
            it.get()
        }
    }

    override fun stop() {
        super.stop()
        projectScopeListener?.let {
            appScope.projectScopesProperty.removeListener(it)
        }
        projectViews.forEach { it.close() }
        appScope.close()
        appScope.coroutineContext.cancel()
    }
}