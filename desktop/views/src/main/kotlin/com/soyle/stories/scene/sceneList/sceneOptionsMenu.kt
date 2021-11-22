package com.soyle.stories.scene.sceneList

import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.create.CreateNewSceneController
import com.soyle.stories.scene.delete.DeleteSceneController
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.reorder.ReorderSceneController
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
        action { viewListener.editScene(sceneItemViewModel.id.uuid.toString(), sceneItemViewModel.proseId) }
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
    MenuItem("Outline").apply {
        id = "open_scene_outline"
        action { viewListener.outlineScene(sceneItemViewModel) }
    },
    SeparatorMenuItem(),
    Menu("Insert").apply {
        item("New Scene Before") {
            action { scope.get<CreateNewSceneController>().before(sceneItemViewModel.id) }
        }
        item("New Scene After") {
            action { scope.get<CreateNewSceneController>().after(sceneItemViewModel.id) }
        }
    },
    MenuItem("Move Up").apply  {
        action {
            scope.get<ReorderSceneController>().reorderScene(sceneItemViewModel.id, sceneItemViewModel.index - 1)
        }
    },
    MenuItem("Move Down").apply  {
        action {
            scope.get<ReorderSceneController>().reorderScene(sceneItemViewModel.id, sceneItemViewModel.index + 1)
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
        action { scope.get<DeleteSceneController>().deleteScene(sceneItemViewModel.id) }
    }
)