package com.soyle.stories.storyevent.list

import com.soyle.stories.common.boundProperty
import com.soyle.stories.common.builders.build
import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.common.components.buttons.ButtonStyles.Companion.inviteButton
import com.soyle.stories.common.components.buttons.ButtonStyles.Companion.noArrow
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.dataDisplay.list.ListStyles
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.elevated
import com.soyle.stories.common.components.surfaces.elevation
import com.soyle.stories.common.components.surfaces.elevationVariant
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.common.emptyProperty
import com.soyle.stories.common.existsWhen
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.details.StoryEventDetails
import com.soyle.stories.storyevent.details.StoryEventDetailsPrompt
import com.soyle.stories.storyevent.details.StoryEventDetailsViewModel
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.lang.ref.WeakReference

class StoryEventListToolView(
    private val projectScope: ProjectScope,
    private val controller: StoryEventListViewActions,
    private val viewModel: ObservableValue<StoryEventListViewModel>,

    private val StoryEventListCell: StoryEventListCell
) : VBox() {

    private val isEmpty = viewModel.booleanBinding { it == null || !it.isPopulated }

    init {
        id = StoryEventListStyles.storyEventList.name
        toggleClass(Stylesheet.empty, isEmpty)
        dynamicContent(viewModel) {
            when (it) {
                null -> Unit
                is EmptyStoryEventListViewModel -> Empty()
                is FailedStoryEventListViewModel -> Failed()
                is LoadingStoryEventListViewModel -> Loading()
                is PopulatedStoryEventListViewModel -> Populated(it)
            }
        }
    }

    private inner class Loading {
        init {
            progressindicator { progress = -1.0 }
        }
    }

    private inner class Failed {
        init {
            toolTitle("Failed to Load Story Events") {
                textAlignment = TextAlignment.CENTER
            }
            button("Retry") {
                addClass(primary, filled, inviteButton)
                action(controller::loadStoryEvents)
            }
        }
    }

    private inner class Empty {
        init {
            toolTitle("No Story Events Yet") {
                textAlignment = TextAlignment.CENTER
            }
            button("Create New Story Event") {
                addClass(primary, filled, inviteButton)
                id = "create-story-event"
                action(controller::createStoryEvent)
            }
        }
    }

    private inner class Populated(private val populatedViewModel: PopulatedStoryEventListViewModel) {

        init {
            stackpane {
                vgrow = Priority.ALWAYS
                vbox {
                    elevation = Elevation.getValue(8)
                    header()
                    list()
                }

                val storyEventDetailsViewModel = StoryEventDetailsViewModel(
                    populatedViewModel.focusedStoryEvent(),
                    projectScope.get()
                )
                vbox {
                    elevation = Elevation.getValue(16)
                    existsWhen(populatedViewModel.focusedStoryEvent().isNotNull)
                }.build {
                    hbox(
                        alignment = Pos.CENTER_RIGHT
                    ) {
                        style { backgroundColor += Color.WHITE }
                        padding = Insets(8.0)
                        primaryButton("DONE") {
                            action { populatedViewModel.focusOn(null) }
                        }
                    }
                    add(StoryEventDetails(storyEventDetailsViewModel))
                }
            }
        }

        private fun Parent.header() {
            hbox {
                style { padding = box(8.px) }
                elevation = Elevation.getValue(12)
                elevationVariant = elevated(objectProperty(Elevation.getValue(4)))
                button("Create New Story Event") {
                    addClass(primary, filled)
                    id = "create-story-event"
                    action(controller::createStoryEvent)
                }
                spacer()
                menubutton("Options") {
                    addClass(secondary, outlined, noArrow)
                    disableWhen(populatedViewModel.selectedItems.emptyProperty())
                    items.setAll(storyEventOptions())
                }
            }
        }

        private fun Parent.list() {
            listview<StoryEventListItemViewModel>(populatedViewModel.items) {
                elevation = Elevation.getValue(8)
                addClass(ListStyles.noCellShading)
                selectionModel.selectionMode = SelectionMode.MULTIPLE
                populatedViewModel.selectedItems.bindTo(selectionModel.selectedItems)
                vgrow = Priority.ALWAYS
                setCellFactory {
                    StoryEventListCell().apply {
                        addClass(StoryEventListStyles.storyEventItem)
                        toggleClass(StoryEventListStyles.equalTime, itemProperty().select { it.prevItemHasSameTime })
                        contextMenuProperty().bind(itemProperty().objectBinding { storyEventContextMenu.takeIf { item != null } })
                        onDoubleClick { item?.let { controller.viewDetails(item.id) } }
                    }
                }
            }
        }

        private val storyEventContextMenu = ContextMenu().apply {
            items.setAll(storyEventOptions())
        }

        private fun storyEventOptions(): List<MenuItem> {
            return Menu().apply {
                menu("Insert New Story Event") {
                    id = "insert"
                    enableWhen(populatedViewModel.hasSingleSelection)
                    item("Before") {
                        id = "before"
                        action(controller::insertStoryEventBeforeSelectedItem)
                    }
                    item("At the Same Time") {
                        id = "at-the-same-time-as"
                        action(controller::insertStoryEventAtSameTimeAsSelectedItem)
                    }
                    item("After") {
                        id = "after"
                        action(controller::insertStoryEventAfterSelectedItem)
                    }
                }
                separator()
                item("Rename") {
                    id = "rename"
                    enableWhen(populatedViewModel.hasSingleSelection)
                    action(controller::renameSelectedItem)
                }
                item("Reschedule") {
                    id = "reschedule"
                    enableWhen(populatedViewModel.hasSingleSelection)
                    action(controller::rescheduleSelectedItem)
                }
                item("Adjust Time") {
                    id = "adjust"
                    action(controller::adjustTimesOfSelectedItems)
                }
                item("Delete") {
                    id = "delete"
                    action(controller::deleteSelectedItems)
                }
                separator()
                menu("Covered By...") {
                    id = "coverage"
                    lazyLoadSceneItems()
                }
                separator()
                item("Details") {
                    action { controller.viewSelectedItemDetails() }
                }
                item("View in Timeline") {
                    id = "view-in-timeline"
                    enableWhen(populatedViewModel.hasSingleSelection)
                    action(controller::viewSelectedItemInTimeline)
                }
            }.items
        }

        private fun Menu.lazyLoadSceneItems() {
            // using vbox and custom menu item because menu doesn't re-render while showing if you change the menu items

            val loadingLabel = Label("Loading...").apply { id = "loading" }
            val optionsMenu = VBox(loadingLabel)
            val item = CustomMenuItem(optionsMenu, true)
            items.setAll(item)

            showingProperty().onChange { populatedViewModel.requestingScenesToCover().set(it) }
            populatedViewModel.scenesToCover().onChange {
                if (! isShowing) return@onChange
                if (it != null) {
                    if (it.isEmpty()) optionsMenu.children.setAll(Label("No scenes available"))
                    else {
                        optionsMenu.children.setAll(it.map { menuItem ->
                            RadioButton(menuItem.text).apply {
                                id = menuItem.id
                                onAction = menuItem.onAction
                                isSelected = (menuItem as? RadioMenuItem)?.isSelected ?: false
                            }
                        })
                    }
                } else optionsMenu.children.setAll(loadingLabel)
            }
            addEventHandler(MenuButton.ON_HIDDEN) { optionsMenu.children.setAll(loadingLabel) }
        }

    }

    private fun <T> ObservableList<T>.bindTo(source: ObservableList<T>) {
        source.addListener(object : ListChangeListener<T> {
            val targetRef = WeakReference(this@bindTo)
            override fun onChanged(change: ListChangeListener.Change<out T>?) {
                val list = targetRef.get()
                if (list == null) {
                    change?.list?.removeListener(this)
                } else {
                    list.setAll(change?.list.orEmpty())
                }
            }
        })
    }

    override fun getUserAgentStylesheet(): String = StoryEventListStyles().externalForm

}