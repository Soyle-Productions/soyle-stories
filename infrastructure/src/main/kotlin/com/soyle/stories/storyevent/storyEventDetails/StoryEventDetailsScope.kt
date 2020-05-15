package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.di.DI
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.StoryEventDetailsToolViewModel
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class StoryEventDetailsScope(val projectScope: ProjectScope, storyEventDetailsToolViewModel: StoryEventDetailsToolViewModel) : Scope()
{

	val storyEventId = storyEventDetailsToolViewModel.storyEventId

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