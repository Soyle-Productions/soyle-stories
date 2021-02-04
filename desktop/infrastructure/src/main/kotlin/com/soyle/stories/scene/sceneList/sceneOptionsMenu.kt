package com.soyle.stories.scene.sceneList

import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.SceneTargeted
import com.soyle.stories.scene.createSceneDialog.createSceneDialog
import com.soyle.stories.scene.deleteSceneDialog.deleteSceneDialog
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialog
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.util.Duration
import tornadofx.*

internal fun sceneOptionsMenu(viewListener: SceneListViewListener, scope: ProjectScope, sceneItemViewModel: SceneItemViewModel): List<MenuItem> = listOf(
    MenuItem("Edit").apply {
        id = "edit"
        toggleClass(SceneListItem.Styles.hasIssue, sceneItemViewModel.invalidEntitiesMentioned)
        action { viewListener.editScene(sceneItemViewModel.id, sceneItemViewModel.proseId) }
    },
    MenuItem("Inspect Details").apply {
        id = "open_details"
        action { viewListener.openSceneDetails(sceneItemViewModel.id) }
    },
    MenuItem("Track Symbols").apply {
        id = "open_scene_symbols"
        toggleClass(SceneListItem.Styles.hasIssue, sceneItemViewModel.unusedSymbols)
        action {
            FX.eventbus.fire(SceneTargeted(sceneItemViewModel))
            viewListener.trackSymbols(sceneItemViewModel)
            runLater(Duration(250.0)) { FX.eventbus.fire(SceneTargeted(sceneItemViewModel)) }
        }
    },
    SeparatorMenuItem(),
    Menu("Insert").apply {
        item("New Scene Before") {
            action { createSceneDialog(scope, sceneItemViewModel.id, true) }
        }
        item("New Scene After") {
            action { createSceneDialog(scope, sceneItemViewModel.id, false) }
        }
    },
    MenuItem("Move Up").apply  {
        action {
            scope.get<ReorderSceneDialog>().show(sceneItemViewModel.id, sceneItemViewModel.name, sceneItemViewModel.index - 1)
        }
    },
    MenuItem("Move Down").apply  {
        action {
            scope.get<ReorderSceneDialog>().show(sceneItemViewModel.id, sceneItemViewModel.name, sceneItemViewModel.index + 1)
        }
    },
    SeparatorMenuItem(),
    MenuItem("Rename").apply {
        id = "rename"
        action {
            scope.get<SceneListModel>().editingItem.value = sceneItemViewModel
        }
    },
    MenuItem("Delete").apply {
        id = "delete"
        action {deleteSceneDialog(scope, sceneItemViewModel) }
    }
)