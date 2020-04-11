package com.soyle.stories.project.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

interface ProjectEvents {

	val openProject: Notifier<OpenProject.OutputPort>
	val closeProject: Notifier<RequestCloseProject.OutputPort>
	val startNewProject: Notifier<StartNewLocalProject.OutputPort>

}