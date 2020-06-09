package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.di.DI
import com.soyle.stories.layout.config.dynamic.StoryEventDetails
import com.soyle.stories.project.ProjectScope
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class StoryEventDetailsScope(val projectScope: ProjectScope, tool: StoryEventDetails) : Scope()
{

	val storyEventId = tool.storyEventId.toString()

	init {
		projectScope.addScope(storyEventId, this)
	}

	fun close() {
		FX.getComponents(this).forEach { (_, it) ->
			if (it is EventTarget) it.removeFromParent()
		}
		deregister()
		DI.deregister(this)
		projectScope.removeScope(storyEventId, this)
	}

}