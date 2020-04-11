package com.soyle.stories.soylestories

import com.soyle.stories.common.launchTask
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.modules.ApplicationComponent
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.confirmExitDialog.ConfirmExitDialog
import com.soyle.stories.soylestories.welcomeScreen.WelcomeScreen
import javafx.collections.ObservableList
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import java.util.*

fun main(args: Array<String>) {
    launch<SoyleStories>(args)
}

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 2:50 PM
 */
class SoyleStories : App(WelcomeScreen::class, WizardStyles::class) {

    private val coroutineScope = CoroutineScope(
        Job() + Dispatchers.JavaFx
    )

    private val projectListViewListener: ProjectListViewListener
        get() = find<ApplicationComponent>().projectListViewListener

    private val applicationModel = find<ApplicationModel>()

    private val projectScopes = mutableMapOf<UUID, ProjectScope>()

    override fun shouldShowPrimaryStage(): Boolean = false

    override fun start(stage: Stage) {
        super.start(stage)

        stage.icons += appIcon

        find<SplashScreen>()
        find<FailedProjectsDialog>()
        find<ConfirmExitDialog>()
        find<OpenProjectOptionDialog>()

        applicationModel.isInvalidated.onChangeWithCurrent {
            if (it != false) {
                val loadTask = launchTask { task ->
                    task.updateProgress(0.0, 100.0)
                    listOf(
                        "Getting started",
                        "Hold on, making a sandwich",
                        "Ok, I'm back... what were we doing?",
                        "Oh yeah, preparing everything else..."
                    ).forEachIndexed { index, s ->
                        task.updateProgress(index * 25.0, 100.0)
                        task.updateMessage(s)
                        delay(1250)
                    }
                    projectListViewListener.startApplicationWithParameters(parameters.raw)
                    withContext(Dispatchers.JavaFx) {
                        applicationModel.openProjects.onChangeWithCurrent { _: ObservableList<ProjectFileViewModel>? ->
                            updateProjectScopes()
                        }
                    }
                }
                applicationModel.initializationMessage.cleanBind(loadTask.messageProperty())
                applicationModel.initializationProgress.cleanBind(loadTask.progressProperty())
            }
        }


    }

    private fun updateProjectScopes() {
        val projectIds = applicationModel.openProjects.asSequence().map { it.projectId }.toSet()
        applicationModel.openProjects.forEach {
            projectScopes.getOrPut(it.projectId) { ProjectScope(it) }
        }
        projectScopes.entries.removeIf {
            if (it.key !in projectIds) {
                FX.getComponents(it.value).forEach { (_, scopedInstance) ->
                    (scopedInstance as? UIComponent)?.close()
                }
                it.value.deregister()
                true
            } else false
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