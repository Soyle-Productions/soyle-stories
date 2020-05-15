package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Character
import com.soyle.stories.layout.entities.StackSplitter
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.entities.ToolStack
import com.soyle.stories.layout.entities.Window
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 11:17 PM
 */

class StaticTool(val toolId: UUID, val isOpen: Boolean, val toolType: KClass<out Tool<*>>)
class OpenWindow(val windowId: UUID, val isPrimary: Boolean, val child: OpenWindowChild)

sealed class OpenWindowChild
class OpenToolGroupSplitter(
    val splitterId: UUID,
    val orientation: Boolean,
    val children: List<Pair<Int, OpenWindowChild>>
) : OpenWindowChild()

class OpenToolGroup(val groupId: UUID, val focusedToolId: UUID?, val tools: List<OpenTool>) : OpenWindowChild()

sealed class OpenTool {
    abstract val toolId: UUID
}
class CharacterListTool(override val toolId: UUID) : OpenTool()
class LocationListTool(override val toolId: UUID) : OpenTool()
class SceneListTool(override val toolId: UUID) : OpenTool()
class StoryEventListTool(override val toolId: UUID) : OpenTool()
class BaseStoryStructureTool(override val toolId: UUID, val characterId: UUID, val themeId: UUID) : OpenTool()
class CharacterComparisonTool(override val toolId: UUID, val characterId: UUID, val themeId: UUID) : OpenTool()
class LocationDetailsTool(override val toolId: UUID, val locationId: UUID) : OpenTool()

fun Window.toActiveWindow() = OpenWindow(id.uuid, isPrimary, child.toActiveWindowChild())

fun Window.WindowChild.toActiveWindowChild(): OpenWindowChild = when (this) {
    is StackSplitter -> OpenToolGroupSplitter(id.uuid, orientation, children.filter { it.second.isOpen }.map { it.first to it.second.toActiveWindowChild() })
    is ToolStack -> OpenToolGroup(id.uuid, focusedTool?.uuid, tools.filter { it.isOpen }.map { it.toOpenTool() })
    else -> error("unexpected window child type $this")
}

fun Tool<*>.toOpenTool() = when (this) {
    is Tool.CharacterList -> CharacterListTool(id.uuid)
    is Tool.LocationList -> LocationListTool(id.uuid)
    is Tool.SceneList -> SceneListTool(id.uuid)
    is Tool.StoryEventList -> StoryEventListTool(id.uuid)
    is Tool.BaseStoryStructure -> BaseStoryStructureTool(id.uuid, identifyingData.second.uuid, identifyingData.first.uuid)
    is Tool.CharacterComparison -> CharacterComparisonTool(id.uuid, (associatedData as Character.Id).uuid, identifyingData.uuid)
    is Tool.LocationDetails -> LocationDetailsTool(id.uuid, identifyingData.uuid)
}

fun Tool<*>.toStaticTool() = StaticTool(id.uuid, isOpen, this::class)