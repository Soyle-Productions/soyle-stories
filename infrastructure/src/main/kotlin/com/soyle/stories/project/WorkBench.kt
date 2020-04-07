package com.soyle.stories.project

import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.launchTask
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.modules.ApplicationComponent
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.project.layout.GroupSplitter
import com.soyle.stories.project.layout.GroupSplitterViewModel
import com.soyle.stories.project.layout.ToolGroup
import com.soyle.stories.project.layout.ToolGroupViewModel
import com.soyle.stories.project.startProjectDialog.startProjectDialog
import com.soyle.stories.soylestories.SoyleStories
import javafx.scene.Parent
import javafx.stage.Screen
import kotlinx.coroutines.runBlocking
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:01 PM
 */
class WorkBench : View() {

    override val scope: ProjectScope = super.scope as ProjectScope

    private val projectViewListener = find<ApplicationComponent>(scope = FX.defaultScope).projectListViewListener
    private val layoutViewListener = find<LayoutComponent>().layoutViewListener
    private val model = find<WorkBenchModel>()

    override val root: Parent = borderpane {
        top = menubar {
            menu("File") {
                menu("New", "Shortcut+N") {
                    item("Project") {
                        action { startProjectDialog(currentStage) }
                    }
                    separator()
                    item("Character") {
                        action { createCharacterDialog(currentStage) }
                    }/*
                    item("Plot Point") {
                        // action { controller.createPlotPoint() }
                    }
                    item("Note", "Shortcut+Shift+N")
                    item("Section") {
                        // action { controller.createSection() }
                    }*/
                }
            }
            menu("Edit") { }
            menu("View") { }
            menu("Tools") {
                items.bind(model.staticTools) {
                    checkmenuitem(messages[it.name]) {
                        isSelected = it.isOpen
                        action {
                            launchTask { _ ->
                                layoutViewListener.toggleToolOpen(it.toolId)
                            }
                        }
                    }
                }
            }
        }
        model.primaryWindow.onChange { window ->
            val centerComponent = center?.uiComponent<UIComponent>()
            center = when (val child = window?.child) {
                is GroupSplitterViewModel -> {
                    (centerComponent as? GroupSplitter ?: find {
                        this@borderpane += this
                    }).let {
                        it.viewModel = child
                        it.root
                    }
                }
                is ToolGroupViewModel -> {
                    (centerComponent as? ToolGroup ?: find {
                        this@borderpane += this
                    }).let {
                        it.viewModel = child
                        it.root
                    }
                }
                null -> {
                    children -= centerComponent?.root
                    null
                }
            }
        }
    }

    init {
        titleProperty.bind(model.projectViewModel.stringBinding {
            it?.run {
                "$name [$location]"
            }
        })
        model.loadingProgress.onChangeUntil({ it is Double && it >= WorkBenchModel.MAX_LOADING_VALUE }) {
            if (it is Double && it >= WorkBenchModel.MAX_LOADING_VALUE) {
                createWindow()
            }
        }
    }

    private fun createWindow() {
        openWindow(escapeClosesWindow = false, owner = null, block = false, resizable = true)?.apply {
            icons += SoyleStories.appIcon
            val primaryScreen = Screen.getScreensForRectangle(primaryStage.x, primaryStage.y, primaryStage.width, primaryStage.height)
            primaryScreen.firstOrNull()?.visualBounds?.let {
                x = it.minX
                y = it.minY
                width = it.width.times(0.8)
                height = it.height.times(0.8)
            }
            centerOnScreen()
            setOnCloseRequest {
                model.projectViewModel.value?.let {
                    runBlocking {
                        projectViewListener.requestCloseProject(it.projectId)
                    }
                }
                it.consume()
            }
        }
    }

}