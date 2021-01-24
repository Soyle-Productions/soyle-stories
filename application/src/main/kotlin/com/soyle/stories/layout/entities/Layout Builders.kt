package com.soyle.stories.layout.entities

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.tools.TemporaryTool
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 11:02 AM
 */

@DslMarker
annotation class LayoutDSL

abstract class LayoutPartBuilder<T : Any> {

	protected open fun <R : Any, T : LayoutPartBuilder<R>> initChild(builder: T, init: T.() -> Unit): R {
		builder.init()
		val part = builder.toLayoutPart()
		addChild(part)
		return part
	}

	protected abstract fun toLayoutPart(): T
	protected abstract fun addChild(child: Any)

}

@LayoutDSL
class LayoutBuilder(private val id: Layout.Id, private val projectId: Project.Id) : LayoutPartBuilder<Layout>() {
	val windows = mutableListOf<Window>()

	@LayoutDSL
	fun window(init: WindowBuilder.() -> Unit): Window = initChild(WindowBuilder(id), init)

	override fun addChild(child: Any) {
		windows.add(child as Window)
	}

	override fun toLayoutPart(): Layout {
		return Layout(id, projectId, windows)
	}

}

@LayoutDSL
class WindowBuilder(private val layoutId: Layout.Id) : LayoutPartBuilder<Window>() {
	var child: Window.WindowChild? = null
		private set(value) {
			if (field != null) error("cannot have more than one child per window")
			field = value
		}

	override fun addChild(child: Any) {
		this.child = child as Window.WindowChild
	}

	override fun toLayoutPart(): Window {
		return when (val child = this.child) {
			is StackSplitter -> Window(Window.Id(UUID.randomUUID()), layoutId, child)
			is ToolStack -> Window(Window.Id(UUID.randomUUID()), layoutId, child)
			else -> error("No valid window child $child")
		}
	}

	private fun stackSplitter(orientation: Boolean, build: SplitterBuilder.() -> Unit): StackSplitter =
	  initChild(SplitterBuilder(orientation, layoutId), build)

	private fun toolStack(isPrimary: Boolean, build: ToolStackBuilder.() -> Unit): ToolStack =
		initChild(ToolStackBuilder(layoutId, isPrimary), build)

	@LayoutDSL
	fun horizontalStackSplitter(build: SplitterBuilder.() -> Unit): StackSplitter = stackSplitter(false, build)

	@LayoutDSL
	fun verticalStackSplitter(build: SplitterBuilder.() -> Unit): StackSplitter = stackSplitter(true, build)

	@LayoutDSL
	fun stack(build: ToolStackBuilder.() -> Unit): ToolStack = toolStack(false, build)

	@LayoutDSL
	fun primaryStack(build: ToolStackBuilder.() -> Unit): ToolStack = toolStack(true, build)

}


class SplitterBuilder(private val orientation: Boolean, private val layoutId: Layout.Id) : LayoutPartBuilder<StackSplitter>() {
	val children = mutableListOf<Pair<Int, Window.WindowChild>>()
	private var tempWeight: Int = -1

	override fun addChild(child: Any) {
		children.add(tempWeight to child as Window.WindowChild)
	}

	override fun toLayoutPart(): StackSplitter {
		return StackSplitter(StackSplitter.Id(UUID.randomUUID()), orientation, layoutId, children)
	}

	@LayoutDSL
	fun stackSplitter(weight: Int, build: SplitterBuilder.() -> Unit): StackSplitter {
		tempWeight = weight
		val splitter = initChild(SplitterBuilder(!orientation, layoutId), build)
		tempWeight = -1
		return splitter
	}

	private fun toolStack(weight: Int, isPrimary: Boolean, build: ToolStackBuilder.() -> Unit): ToolStack {
		tempWeight = weight
		val stack = initChild(ToolStackBuilder(layoutId, isPrimary), build)
		tempWeight = -1
		return stack
	}


	@LayoutDSL
	fun stack(weight: Int, build: ToolStackBuilder.() -> Unit): ToolStack =
	  toolStack(weight, false, build)

	@LayoutDSL
	fun primaryStack(weight: Int, build: ToolStackBuilder.() -> Unit): ToolStack =
	  toolStack(weight, true, build)
}

@LayoutDSL
class ToolStackBuilder(private val layoutId: Layout.Id, private val isPrimary: Boolean) : LayoutPartBuilder<ToolStack>() {
	val id = ToolStack.Id(UUID.randomUUID())
	private val tools: List<Tool> = mutableListOf()
	private val markers: List<String> = mutableListOf()

	override fun addChild(child: Any) {
		when (child) {
			is Tool -> (tools as MutableList).add(child)
			is KClass<*> -> (markers as MutableList).add(child.qualifiedName!!)
		}
	}

	override fun toLayoutPart(): ToolStack {
		return ToolStack(id, layoutId, tools, markers, isPrimary, tools.firstOrNull()?.id)
	}

	@LayoutDSL
	fun tool(tool: Tool) = addChild(tool)

	@LayoutDSL
	fun marker(kClass: KClass<out TemporaryTool>) = addChild(kClass)

}

fun layout(projectId: Project.Id, layoutId: Layout.Id, build: LayoutBuilder.() -> Unit): Layout {
	val builder = LayoutBuilder(layoutId, projectId)
	builder.build()
	return Layout(layoutId, projectId, builder.windows.toList())
}