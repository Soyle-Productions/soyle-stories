package com.soyle.stories.layout

import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.StackSplitter
import com.soyle.stories.layout.entities.ToolStack
import com.soyle.stories.layout.entities.Window

fun Any.tracePath(layout: Layout): List<String>
{
	val pathIfFound = listOf(layout.id.toString())
	if (this == layout) return pathIfFound
	layout.windows.forEach {
		val windowPath = tracePath(it)
		if (windowPath.isNotEmpty()) return pathIfFound + windowPath
	}
	return listOf()
}

private fun Any.tracePath(window: Window): List<String>
{
	val pathIfFound = listOf(window.id.toString())
	if (this == window) return pathIfFound
	val childPath = tracePath(window.child)
	if (childPath.isNotEmpty()) return pathIfFound + childPath
	return listOf()
}

private fun Any.tracePath(child: Window.WindowChild): List<String> =
  when (child) {
	  is StackSplitter -> tracePath(child)
	  is ToolStack -> tracePath(child)
	  else -> error("unexpected windowchild type $this")
  }

private fun Any.tracePath(splitter: StackSplitter): List<String>
{
	val pathIfFound = listOf(splitter.id.toString())
	if (this == splitter) return pathIfFound
	splitter.children.forEach {
		val childPath = tracePath(it.second)
		if (childPath.isNotEmpty()) return pathIfFound + childPath
	}
	return listOf()
}

private fun Any.tracePath(stack: ToolStack): List<String>
{
	val pathIfFound = listOf(stack.id.toString())
	if (this == stack) return pathIfFound
	stack.tools.forEach {
		if (this == it) return pathIfFound
	}
	return listOf()
}