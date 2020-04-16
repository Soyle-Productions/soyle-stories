package com.soyle.stories.project.layout

import com.soyle.stories.characterarc.baseStoryStructure.baseStoryStructureTab
import com.soyle.stories.characterarc.characterComparison.characterComparisonTab
import com.soyle.stories.characterarc.characterList.CharacterList
import com.soyle.stories.common.launchTask
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.location.locationList.LocationList
import com.soyle.stories.project.ProjectScope
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
    private val layoutViewListener = find<LayoutComponent>().layoutViewListener


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
                        when (tool) {
                            is CharacterListToolViewModel -> tab<CharacterList>()
                            is LocationListToolViewModel -> tab<LocationList>()
                            is BaseStoryStructureToolViewModel -> baseStoryStructureTab(scope, tool.characterId, tool.themeId)
                            is CharacterComparisonToolViewModel -> characterComparisonTab(scope, tool.themeId, tool.characterId)/*
                            ToolType.Timeline -> Tab("").also { tabs.add(it) }
                            ToolType.NoteList -> Tab().also { tabs.add(it) }
                            ToolType.SceneList -> Tab().also { tabs.add(it) }
                            ToolType.Properties -> Tab().also { tabs.add(it) }
                            ToolType.PlotPointList -> Tab().also { tabs.add(it) }
                            ToolType.SceneWeave -> Tab().also { tabs.add(it) }
                            ToolType.ContinuityErrors -> Tab().also { tabs.add(it) }
                            ToolType.CharacterDevelopment -> Tab().also { tabs.add(it) }
                            ToolType.LocationTracking -> Tab().also { tabs.add(it) }*/
                        }.also {
                            it.setOnCloseRequest {
                                launchTask {
                                    layoutViewListener.closeTool(tool.toolId)
                                }
                                it.consume()
                            }
                            selectionModel.select(it)
                        }
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