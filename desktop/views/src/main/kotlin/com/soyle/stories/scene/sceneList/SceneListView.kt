package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.components.buttons.inviteButton
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.create.CreateNewSceneController
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.target.TargetScene
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.effect.InnerShadow
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.util.*

class SceneListView : View() {

    override val scope: ProjectScope = super.scope as ProjectScope

    private val model = resolve<SceneListModel>()

    private val viewListener = resolve<SceneListViewListener>()

    val sceneContextMenu = ContextMenu().apply {
        model.selectedItem.onChange {
            if (it == null) items.clear()
            else items.setAll(sceneOptionsMenu(viewListener, scope, it))
        }
    }

    override val root: Parent = stackpane {
        hgrow = Priority.SOMETIMES
        vgrow = Priority.ALWAYS
        dynamicContent(model.hasScenes) {
            if (it == true) populatedDisplay()
            else emptyDisplay()
        }
    }

    private fun Parent.emptyDisplay() = vbox(spacing = 16.0) {
        style { padding = box(16.px) }
        addClass("empty-display")
        hgrow = Priority.SOMETIMES
        vgrow = Priority.ALWAYS
        alignment = Pos.CENTER
        imageview("com/soyle/stories/scene/Scenes-Icon.png") {
            alignment = Pos.CENTER
        }
        toolTitle("Scenes") {
            textAlignment = TextAlignment.CENTER
        }
        label("Scenes are where your story happens.  Create your first scene by clicking the button below and get started!") {
            textAlignment = TextAlignment.CENTER
            isWrapText = true
        }
        inviteButton(model.createSceneButtonLabel) {
            addClass("center-button")
            alignment = Pos.CENTER
            isMnemonicParsing = false
            action { scope.get<CreateNewSceneController>().create() }
        }
    }
    private fun Parent.populatedDisplay() = vbox {
        visibleWhen { model.hasScenes }
        managedProperty().bind(visibleProperty())
        minWidth = 200.0
        minHeight = 100.0
        vgrow = Priority.ALWAYS
        hbox(spacing = 10.0) {
            style {
                isFillHeight = false
                padding = box(8.px)
                backgroundColor += Color.WHITE
                effect = DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.25), 4.0, 0.0, 0.0, 4.0)
            }
            primaryButton(model.createSceneButtonLabel) {
                id = "actionBar_create"
                isDisable = false
                action {
                    scope.get<CreateNewSceneController>().create()
                }
                isMnemonicParsing = false
            }
            spacer()
            menubutton("Options") {
                enableWhen { model.selectedItem.isNotNull }
                model.selectedItem.onChange {
                    if (it == null) items.clear()
                    else items.setAll(sceneOptionsMenu(viewListener, scope, it))
                }
            }
            viewOrder = 0.0
        }
        treeview<SceneItemViewModel?>(TreeItem(null)) {
            viewOrder = 1.0

            isShowRoot = false
            vgrow = Priority.ALWAYS
            selectionModel.selectedItemProperty().onChange {
                val selectedItem = it?.value
                model.selectedItem.value = selectedItem
                if (selectedItem != null) {
                    scope.get<TargetScene>().invoke(
                        selectedItem.id,
                        selectedItem.proseId,
                        selectedItem.name
                    )
                }
            }
            model.selectedItem.onChange { newSelection ->
                selectionModel.select(root.children.find { it.value?.id == newSelection?.id })
            }
            contextMenu = sceneContextMenu
            cellFragment(scope, SceneListItem::class)
            model.scenes.onChange { scenes: ObservableList<SceneItemViewModel>? ->
                val currentlyFocused = isFocused
                val currentSelection = model.selectedItem.value?.id
                val newItems = setSceneItems(scenes)
                if (currentSelection != null) {
                    selectionModel.select(newItems.find { it.value.id == currentSelection } as? TreeItem<SceneItemViewModel?>)
                }
                if (currentlyFocused) {
                    requestFocus()
                }
            }
            setSceneItems(model.scenes)
        }
    }

    private fun TreeView<SceneItemViewModel?>.setSceneItems(scenes: ObservableList<SceneItemViewModel>?): List<TreeItem<SceneItemViewModel>> {
        val newItems = scenes?.map { TreeItem(it) } ?: listOf()
        root.children.setAll(newItems)
        return newItems
    }

    init {
        titleProperty.bind(model.toolTitle)

        viewListener.getValidState()
    }

    class Styles : Stylesheet() {
        companion object {
            val draggingCell by cssclass()

            init {
                importStylesheet(Styles::class)
            }
        }

        init {
            draggingCell {
                backgroundColor += Color.GREY
                textFill = Color.GREY
                effect = InnerShadow(BlurType.THREE_PASS_BOX, Color.BLACK, 2.0, 0.0, 0.0, 1.0)
            }
        }
    }

}