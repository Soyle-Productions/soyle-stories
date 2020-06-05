package com.soyle.stories.soylestories

import com.soyle.stories.common.async
import com.soyle.stories.di.DI
import com.soyle.stories.di.configureDI
import com.soyle.stories.di.get
import com.soyle.stories.project.WorkBench
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.confirmExitDialog.ConfirmExitDialog
import com.soyle.stories.soylestories.welcomeScreen.WelcomeScreen
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*

fun main(args: Array<String>) {
    configureDI()
    launch<SoyleStories>(args)
}

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 2:50 PM
 */
class SoyleStories : App(WelcomeScreen::class, WizardStyles::class) {

    private val appScope = ApplicationScope()
    override var scope: Scope = appScope

    private val applicationModel = find<ApplicationModel>(appScope)
    private val projectListViewListener: ProjectListViewListener by DI.resolveLater(appScope)

    val projectViews: ObservableList<WorkBench> = FXCollections.observableArrayList()

    override fun shouldShowPrimaryStage(): Boolean = false

    override fun start(stage: Stage) {

        super.start(stage)

        stage.icons += appIcon

        find<SplashScreen>(appScope)
        find<FailedProjectsDialog>(appScope)
        find<ConfirmExitDialog>(appScope)
        find<OpenProjectOptionDialog>(appScope)

        async(appScope) {
            projectListViewListener.startApplicationWithParameters(parameters.raw)
        }

        projectViews.bind(appScope.projectScopesProperty) {
            it.get()
        }
    }

    companion object {
        val appIcon = Image("com/soyle/stories/soylestories/icon.png")
        val logo = Image("com/soyle/stories/soylestories/bronze logo.png")

        init {
            loadFont("/com/soyle/stories/soylestories/corbel/CORBEL.TTF", 14)!!
            loadFont("/com/soyle/stories/soylestories/corbel/CORBELB.TTF", 14)!!
        }
    }

}