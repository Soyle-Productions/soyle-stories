package com.soyle.stories.layout

import com.soyle.stories.common.async
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
                        ToolModule.getTabConfigFor(tool).getTab(this, scope).also {
                            it.setOnCloseRequest {
                                async(scope) {
                                    layoutViewListener.closeTool(tool.toolId)
                                }
                                it.consume()
                            }
                            receivingUpdate = true
                            selectionModel.select(it)
                            receivingUpdate = false
                        }
                    }
                }
                tabMap.forEach { (t, u) ->
                    if (t !in toolIds) {
                        u.close()
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
            style {
                backgroundColor += Color.LIGHTGRAY
            }
        }
    }
}