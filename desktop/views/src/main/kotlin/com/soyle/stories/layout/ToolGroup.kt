package com.soyle.stories.layout

import com.soyle.stories.common.async
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.surfaces.Surface.Companion.surface
import com.soyle.stories.common.components.surfaces.SurfaceStyles
import com.soyle.stories.di.layout.ToolModule
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.layout.ToolGroupViewModel
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.StackPane
import tornadofx.*

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
    private var receivingUpdate = false

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
                        ToolModule.getTabConfigFor(tool).getTab(this, scope).also { tab ->
                            tab.setOnCloseRequest {
                                async(scope) {
                                    layoutViewListener.closeTool(tool.toolId)
                                }
                                it.consume()
                            }
                            receivingUpdate = true
                            selectionModel.select(tab)
                            receivingUpdate = false
                        }
                    }
                }
                tabMap.forEach { (toolId, tab) ->
                    if (toolId !in toolIds) {
                        tab.close()
                    }
                }
                tabMap.keys.removeIf { it !in toolIds }/*
                vm.focusedToolId?.let {
                    tabMap[it]?.let {
                        selectionModel.select(it)
                    }
                }*/
            }
        }
        label("Random hints") {
            visibleWhen { isEmpty }
            alignment = Pos.CENTER
            fitToParentSize()
        }
    }
}