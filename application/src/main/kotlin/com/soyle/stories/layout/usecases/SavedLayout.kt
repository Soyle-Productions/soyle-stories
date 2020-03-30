package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Character
import com.soyle.stories.layout.entities.*
import java.util.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 11:17 PM
 */

class StaticTool(val toolId: UUID, val isOpen: Boolean)
class ActiveWindow(val windowId: UUID, val isPrimary: Boolean, val child: ActiveWindowChild)

sealed class ActiveWindowChild
class ActiveToolGroupSplitter(
    val splitterId: UUID,
    val orientation: Boolean,
    val children: List<Pair<Int, ActiveWindowChild>>
) : ActiveWindowChild()

class ActiveToolGroup(val groupId: UUID, val focusedToolId: UUID?, val tools: List<ActiveTool>) : ActiveWindowChild()

sealed class ActiveTool {
    abstract val toolId: UUID
}
class CharacterListActiveTool(override val toolId: UUID) : ActiveTool()
class BaseStoryStructureActiveTool(override val toolId: UUID, val characterId: UUID, val themeId: UUID) : ActiveTool()
class CharacterComparisonActiveTool(override val toolId: UUID, val characterId: UUID, val themeId: UUID) : ActiveTool()

fun Window.toActiveWindow() = ActiveWindow(id.uuid, isPrimary, child.toActiveWindowChild())

fun Window.WindowChild.toActiveWindowChild(): ActiveWindowChild = when (this) {
    is StackSplitter -> ActiveToolGroupSplitter(id.uuid, orientation, children.filter { it.second.isOpen }.map { it.first to it.second.toActiveWindowChild() })
    is ToolStack -> ActiveToolGroup(id.uuid, focusedTool?.uuid, tools.filter { it.isOpen }.map { it.toActiveTool() })
    else -> error("unexpected window child type $this")
}

fun Tool<*>.toActiveTool() = when (this) {
    is CharacterListTool -> CharacterListActiveTool(id.uuid)
    is BaseStoryStructureTool -> BaseStoryStructureActiveTool(id.uuid, identifyingData.second.uuid, identifyingData.first.uuid)
    is CharacterComparisonTool -> CharacterComparisonActiveTool(id.uuid, identifyingData.uuid, (associatedData as Character.Id).uuid)
}

fun Tool<*>.toStaticTool() = StaticTool(id.uuid, isOpen)