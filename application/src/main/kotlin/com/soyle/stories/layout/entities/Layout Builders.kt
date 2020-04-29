package com.soyle.stories.layout.entities

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import java.util.*

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 11:02 AM
 */

@DslMarker
annotation class LayoutDSL

class LayoutBuilder(val id: Layout.Id) {
    val windows = mutableListOf<Window>()
}
inline fun layout(projectId: Project.Id, layoutId: Layout.Id, build: LayoutBuilder.() -> Unit): Layout {
    val builder = LayoutBuilder(layoutId)
    builder.build()
    return Layout(layoutId, projectId, builder.windows.toList())
}

class WindowBuilder(val id: Window.Id, val layoutId: Layout.Id)
@LayoutDSL
inline fun LayoutBuilder.window(build: WindowBuilder.() -> Window.WindowChild): Window {
    val builder = WindowBuilder(Window.Id(UUID.randomUUID()), id)
    val window = Window(Window.Id(UUID.randomUUID()), id, builder.build())
    windows += window
    return window
}

class SplitterBuilder(val orientation: Boolean, val layoutId: Layout.Id) {
    val children = mutableListOf<Pair<Int, Window.WindowChild>>()
}
@LayoutDSL
inline fun WindowBuilder.stackSplitter(orientation: Boolean, build: SplitterBuilder.() -> Unit): StackSplitter {
    val builder = SplitterBuilder(orientation, layoutId)
    builder.build()
    return StackSplitter(StackSplitter.Id(UUID.randomUUID()), orientation, layoutId, builder.children.toList())
}
@LayoutDSL
inline fun WindowBuilder.horizontalStackSplitter(build: SplitterBuilder.() -> Unit): StackSplitter = stackSplitter(false, build)
@LayoutDSL
inline fun WindowBuilder.verticalStackSplitter(build: SplitterBuilder.() -> Unit): StackSplitter = stackSplitter(true, build)

@LayoutDSL
inline fun SplitterBuilder.stackSplitter(weight: Int, build: SplitterBuilder.() -> Unit): StackSplitter {
    val builder = SplitterBuilder(! orientation, layoutId)
    builder.build()
    val splitter = StackSplitter(StackSplitter.Id(UUID.randomUUID()), !orientation, layoutId, builder.children.toList())
    children += weight to splitter
    return splitter
}


@LayoutDSL
inline fun WindowBuilder.stack(build: MutableList<Tool<*>>.() -> Unit): ToolStack {
    val tools = mutableListOf<Tool<*>>()
    tools.build()
    return ToolStack(ToolStack.Id(UUID.randomUUID()), layoutId, tools.toList(), false, null)
}
@LayoutDSL
inline fun WindowBuilder.primaryStack(build: MutableList<Tool<*>>.() -> Unit): ToolStack {
    val tools = mutableListOf<Tool<*>>()
    tools.build()
    return ToolStack(ToolStack.Id(UUID.randomUUID()), layoutId, tools.toList(), true, null)
}

@LayoutDSL
inline fun SplitterBuilder.stack(weight: Int, build: MutableList<Tool<*>>.() -> Unit): ToolStack {
    val tools = mutableListOf<Tool<*>>()
    tools.build()
    val stack = ToolStack(ToolStack.Id(UUID.randomUUID()), layoutId, tools.toList(), false, null)
    this.children += weight to stack
    return stack
}
@LayoutDSL
inline fun SplitterBuilder.primaryStack(weight: Int, build: MutableList<Tool<*>>.() -> Unit): ToolStack {
    val tools = mutableListOf<Tool<*>>()
    tools.build()
    val stack = ToolStack(ToolStack.Id(UUID.randomUUID()), layoutId, tools.toList(), true, null)
    this.children += weight to stack
    return stack
}

@LayoutDSL
inline fun MutableList<Tool<*>>.openTool(type: ToolType, associatedData: Map<String, Any?> = mapOf()): Tool<*> {
    val tool = when (type) {
        ToolType.CharacterComparison -> CharacterComparisonTool(Tool.Id(UUID.randomUUID()), associatedData["themeId"] as Theme.Id, associatedData["characterId"] as Character.Id?, true)
        ToolType.BaseStoryStructure -> BaseStoryStructureTool(Tool.Id(UUID.randomUUID()), associatedData["themeId"] as Theme.Id, associatedData["characterId"] as Character.Id, true)
        ToolType.CharacterList -> CharacterListTool(Tool.Id(UUID.randomUUID()), associatedData["projectId"] as Project.Id, true)
        ToolType.LocationList -> LocationListTool(Tool.Id(UUID.randomUUID()), associatedData["projectId"] as Project.Id, true)
        else -> error("unsupported tool type $type")
    }
    this += tool
    return tool
}
@LayoutDSL
inline fun MutableList<Tool<*>>.tool(type: ToolType, associatedData: Map<String, Any?> = mapOf()): Tool<*> {
    val tool = when (type) {
        ToolType.CharacterComparison -> CharacterComparisonTool(Tool.Id(UUID.randomUUID()), associatedData["themeId"] as Theme.Id, associatedData["characterId"] as Character.Id?, false)
        ToolType.BaseStoryStructure -> BaseStoryStructureTool(Tool.Id(UUID.randomUUID()), associatedData["themeId"] as Theme.Id, associatedData["characterId"] as Character.Id, false)
        ToolType.CharacterList -> CharacterListTool(Tool.Id(UUID.randomUUID()), associatedData["projectId"] as Project.Id, false)
        ToolType.LocationList -> LocationListTool(Tool.Id(UUID.randomUUID()), associatedData["projectId"] as Project.Id, false)
        else -> error("unsupported tool type $type")
   }
    this += tool
    return tool
}

