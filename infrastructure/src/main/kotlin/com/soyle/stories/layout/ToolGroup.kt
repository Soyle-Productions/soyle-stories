package com.soyle.stories.layout

import com.soyle.stories.characterarc.baseStoryStructure.baseStoryStructureTab
import com.soyle.stories.characterarc.characterComparison.characterComparisonTab
import com.soyle.stories.characterarc.characterList.CharacterList
import com.soyle.stories.common.async
import com.soyle.stories.di.resolve
import com.soyle.stories.layout.tools.dynamic.*
import com.soyle.stories.layout.tools.fixed.FixedTool
import com.soyle.stories.layout.tools.temporary.TemporaryTool
import com.soyle.stories.location.locationDetails.locationDetailsTab
import com.soyle.stories.location.locationList.LocationList
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.layout.ToolGroupViewModel
import com.soyle.stories.scene.sceneList.SceneList
import com.soyle.stories.storyevent.storyEventDetails.storyEventDetailsTab
import com.soyle.stories.storyevent.storyEventList.StoryEventList
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.paint.Color
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 4:08 PM
 */
class ToolGroup : WindowChild() {

    override val scope = super.scope as ProjectScope
    private val layoutViewListener = resolve<LayoutViewListener>()

    private val tabMap = mutableMapOf<String, Tab>()

    private val viewModelProperty = SimpleObjectProperty<ToolGroupViewModel?>(null)

    var viewModel: ToolGroupViewModel?
        get() = viewModelProperty.value
        set(value) {
            viewModelProperty.set(value)
        }

    private val isEmpty = viewModelProperty.booleanBinding { it?.tools.isNullOrEmpty() }

    override val root: Parent = stackpane {
        tabpane {
            hiddenWhen { isEmpty }
            fitToParentSize()
            side = Side.TOP
            //find<DraggingTabPaneSupport>().addSupport(this)
            tabDragPolicy = TabPane.TabDragPolicy.REORDER
            tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS
            viewModelProperty.onChange { vm ->
                if (vm == null) return@onChange
                val toolIds = vm.tools.map { it.toolId }.toSet()
                vm.tools.map { tool ->
                    tabMap.getOrPut(tool.toolId) {
                        val type = tool.type
                        when (type) {
                            is FixedTool -> when (type) {
                                FixedTool.CharacterList -> tab<CharacterList>()
                                FixedTool.LocationList -> tab<LocationList>()
                                FixedTool.SceneList -> tab<SceneList>()
                                FixedTool.StoryEventList -> tab<StoryEventList>()
                            }
                            is DynamicTool -> when (type) {
                                is BaseStoryStructure -> baseStoryStructureTab(scope, tool.toolId, type)
                                is CharacterComparison -> characterComparisonTab(scope, type.themeId.toString(), type.characterId.toString())
                                is LocationDetails -> locationDetailsTab(scope, type)
                                is StoryEventDetails -> storyEventDetailsTab(scope, type)
                                is TemporaryTool -> when (type) {
                                    else -> kotlin.error("")
                                }
                                else -> kotlin.error("")
                            }
                            else -> kotlin.error("")
                        }.also {
                            it.setOnCloseRequest {
                                async(scope) {
                                    layoutViewListener.closeTool(tool.toolId)
                                }
                                it.consume()
                            }
                            selectionModel.select(it)
                        }
                        /*

                        when (tool) {
                            is CharacterListToolViewModel -> tab<CharacterList>()
                            is LocationListToolViewModel -> tab<LocationList>()
                            is SceneListToolViewModel -> tab<SceneList>()
                            is StoryEventListToolViewModel -> tab<StoryEventList>()

                            is BaseStoryStructureToolViewModel -> baseStoryStructureTab(scope, tool)
                            is CharacterComparisonToolViewModel -> characterComparisonTab(scope, tool.themeId, tool.characterId)
                            is LocationDetailsToolViewModel -> locationDetailsTab(scope, tool)
                            is StoryEventDetailsToolViewModel -> storyEventDetailsTab(scope, tool)

                            ToolType.Timeline -> Tab("").also { tabs.add(it) }
                            ToolType.NoteList -> Tab().also { tabs.add(it) }
                            ToolType.SceneList -> Tab().also { tabs.add(it) }
                            ToolType.Properties -> Tab().also { tabs.add(it) }
                            ToolType.PlotPointList -> Tab().also { tabs.add(it) }
                            ToolType.SceneWeave -> Tab().also { tabs.add(it) }
                            ToolType.ContinuityErrors -> Tab().also { tabs.add(it) }
                            ToolType.CharacterDevelopment -> Tab().also { tabs.add(it) }
                            ToolType.LocationTracking -> Tab().also { tabs.add(it) }
                        }
                         */
                    }
                }
                tabMap.forEach { (t, u) ->
                    if (t !in toolIds) u.close()
                }
                tabMap.keys.removeIf { it !in toolIds }
                vm.focusedToolId?.let {
                    tabMap[it]?.let {
                        selectionModel.select(it)
                    }
                }
            }
        }
        label("Random hints") {
            visibleWhen { isEmpty }
            alignment = Pos.CENTER
            fitToParentSize()
            style {
                backgroundColor += Color.LIGHTGRAY
            }
        }
    }
}