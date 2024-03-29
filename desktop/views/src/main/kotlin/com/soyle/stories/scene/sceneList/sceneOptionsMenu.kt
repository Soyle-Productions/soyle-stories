package com.soyle.stories.scene.sceneList

import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.createSceneDialog.createSceneDialog
import com.soyle.stories.scene.deleteSceneDialog.deleteSceneDialog
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialog
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import tornadofx.action
import tornadofx.item
import tornadofx.toggleClass

internal fun sceneOptionsMenu(viewListener: SceneListViewListener, scope: ProjectScope, sceneItemViewModel: SceneItemViewModel): List<MenuItem> = listOf(
    MenuItem("Edit").apply {
        id = "edit"
        toggleClass(SceneListItem.Styles.hasIssue, sceneItemViewModel.invalidEntitiesMentioned)
        action { viewListener.editScene(sceneItemViewModel.id, sceneItemViewModel.proseId) }
    },
    MenuItem("Track Characters").apply {
        id = "open_scene_characters"
        toggleClass(SceneListItem.Styles.hasIssue, false)
        action { viewListener.trackCharacters(sceneItemViewModel) }
    },
    MenuItem("Track Locations").apply {
        id = "open_scene_locations"
        toggleClass(SceneListItem.Styles.hasIssue, false)
        action { viewListener.trackLocations(sceneItemViewModel) }
    },
    MenuItem("Track Symbols").apply {
        id = "open_scene_symbols"
        toggleClass(SceneListItem.Styles.hasIssue, sceneItemViewModel.unusedSymbols)
        action { viewListener.trackSymbols(sceneItemViewModel) }
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