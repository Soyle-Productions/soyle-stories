package com.soyle.stories.layout.usecases

import com.soyle.stories.layout.entities.*
import com.soyle.stories.layout.tools.ToolType
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import java.util.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 11:17 PM
 */

interface OpenLayoutPart {
	val id: UUID
}

class OpenTool(override val id: UUID, val toolType: ToolType) : OpenLayoutPart
class OpenWindow(override val id: UUID, val isPrimary: Boolean = true, val child: OpenWindowChild) : OpenLayoutPart

sealed class OpenWindowChild : OpenLayoutPart
class OpenToolGroupSplitter(
  override val id: UUID,
    val orientation: Boolean,
    val children: List<Pair<Int, OpenWindowChild>>
) : OpenWindowChild()

class OpenToolGroup(override val id: UUID, val focusedToolId: UUID?, val tools: List<OpenTool>) : OpenWindowChild()

fun Layout.toResponseModel(): GetSavedLayout.ResponseModel
{
	return GetSavedLayout.ResponseModel(
	  id.uuid,
	  windows.mapNotNull { it.toOpenWindow() },
	  fixedTools.mapNotNull { it.toOpenTool() }
	)
}

fun Window.toOpenWindow(): OpenWindow?
{
	if (! isOpen) return null
	return OpenWindow(
	  id.uuid,
	  child = child.toOpenWindowChild()!!
	)
}

fun Window.WindowChild.toOpenWindowChild(): OpenWindowChild?
{
	if (! isOpen) return null
	return when (this) {
		is StackSplitter -> OpenToolGroupSplitter(id.uuid, orientation, children.mapNotNull { (weight, child) -> child.toOpenWindowChild()?.let { weight to it } })
		is ToolStack -> OpenToolGroup(id.uuid, focusedTool?.uuid, tools.mapNotNull { it.toOpenTool() })
		else -> error("Unrecognized window child $this")
	}
}

fun Tool.toOpenTool(): OpenTool?
{
	if (! isOpen) return null
	return OpenTool(
	  id.uuid,
	  type
	)
}