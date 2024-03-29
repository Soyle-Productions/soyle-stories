package com.soyle.stories.project

import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.async
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.layout.GroupSplitter
import com.soyle.stories.layout.ToolGroup
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.project.dialogs.ActiveDialogsView
import com.soyle.stories.project.layout.GroupSplitterViewModel
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.layout.ToolGroupViewModel
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.project.startProjectDialog.startProjectDialog
import com.soyle.stories.scene.createSceneDialog.createSceneDialog
import com.soyle.stories.soylestories.Styles
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import com.soyle.stories.writer.settingsDialog.SettingsDialog
import javafx.scene.Parent
import javafx.stage.Screen
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:01 PM
 */
class WorkBench : View() {

    override val scope: ProjectScope = super.scope as ProjectScope

    private val projectViewListener = resolve<ProjectListViewListener>(scope = scope.applicationScope)
    private val layoutViewListener = resolve<LayoutViewListener>()
    private val activeDialogsView = resolve<ActiveDialogsView>()
    private val model = resolve<WorkBenchModel>()

    override val root: Parent = borderpane {
        top = menubar {
            menu("File") {
                id = "file"
                menu("New", "Shortcut+N") {
                    id = "file_new"
                    item("Project") {
                        id = "file_new_project"
                        action { startProjectDialog(scope.applicationScope, currentStage) }
                    }
                    separator()
                    item("Character") {
                        id = "file_new_character"
                        action { createCharacterDialog(scope) }
                    }
                    item("Location") {
                        id = "file_new_location"
                        action { scope.get<CreateLocationDialog.Factory>().invoke().show() }
                    }
                    item("Scene") {
                        id = "file_new_scene"
                        action { createSceneDialog(scope) }
                    }
                    item("Theme") {
                        id = "file_new_theme"
                        action { scope.get<CreateThemeDialog>().show(currentWindow) }
                    }
                    item("Symbol") {
                        id = "file_new_symbol"
                        action { scope.get<CreateSymbolDialog>().show(themeId = null, parentWindow = currentWindow) }
                    }
                    /*
                    item("Plot Point") {
                        // action { controller.createPlotPoint() }
                    }
                    item("Note", "Shortcut+Shift+N")
                    item("Section") {
                        // action { controller.createSection() }
                    }*/
                }
                item("Settings") {
                    id = "file_settings"
                    action { scope.get<SettingsDialog>().show() }
                }
            }
            menu("Edit") {
                isDisable = true
            }
            menu("View") {
                isDisable = true
            }
            menu("Tools") {
                id = "tools"
                items.bind(model.staticTools) {
                    checkmenuitem(it.name) {
                        id = "tools_${it.type.toString().toLowerCase()}"
                        isSelected = it.isOpen
                        action {
                            async(scope) {
                                layoutViewListener.toggleToolOpen(it.type)
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
        find<ProjectLoadingDialog>()

        titleProperty.bind(model.projectViewModel.stringBinding {
            it?.run {
                "$name [$location]"
            }
        })
        model.isValidLayout.onChangeUntil({ it == true }) {
            if (it == true) {
                createWindow()
                model.isValidLayout.onChange {
                    if (it != true) {
                        layoutViewListener.loadLayoutForProject(scope.projectId)
                    }
                }
            }
        }

        // moved below model listeners because, if it returns immediately, the listeners aren't attached in time and
        // miss the first update.
        layoutViewListener.loadLayoutForProject(scope.projectId)
    }

    private fun createWindow() {
        openWindow(escapeClosesWindow = false, owner = null, block = false, resizable = true)?.apply {
            icons += Styles.appIcon
            val primaryScreen = Screen.getScreensForRectangle(this.x, this.y, this.width, this.height)
            primaryScreen.firstOrNull()?.visualBounds?.let {
                x = it.minX
                y = it.minY
                width = it.width.times(0.8)
                height = it.height.times(0.8)
            }
            centerOnScreen()
            setOnCloseRequest {
                model.projectViewModel.value?.let {
                    async(scope.applicationScope) {
                        projectViewListener.requestCloseProject(it.projectId)
                    }
                }
                it.consume()
            }
            model.isOpen.onChangeUntil({ it != true }) {
                if (it != true) close()
            }
        }
    }

}