package com.soyle.stories.storyevent.list

import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.common.components.buttons.ButtonStyles.Companion.inviteButton
import com.soyle.stories.common.components.buttons.ButtonStyles.Companion.noArrow
import com.soyle.stories.common.components.dataDisplay.list.ListStyles
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.common.emptyProperty
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.domain.storyevent.StoryEvent
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.lang.ref.WeakReference

class StoryEventListToolView(
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
            header()
            list()
        }

        private fun header() {
            hbox {
                style { padding = box(8.px) }
                asSurface {
                    inheritedElevation = Elevation.get(8)!!
                    relativeElevation = Elevation.get(4)!!
                }
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

        private fun list() {
            listview<StoryEventListItemViewModel>(populatedViewModel.items) {
                asSurface { inheritedElevation = Elevation[8]!! }
                addClass(ListStyles.noCellShading)
                selectionModel.selectionMode = SelectionMode.MULTIPLE
                populatedViewModel.selectedItems.bindTo(selectionModel.selectedItems)
                vgrow = Priority.ALWAYS
                setCellFactory {
                    StoryEventListCell().apply {
                        addClass(StoryEventListStyles.storyEventItem)
                        toggleClass(StoryEventListStyles.equalTime, itemProperty().select { it.prevItemHasSameTime })
                        contextMenuProperty().bind(itemProperty().objectBinding { storyEventContextMenu.takeIf { item != null } })
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
            }.items
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