package com.soyle.studio.workspace.events

import com.soyle.studio.common.DomainEvent
import com.soyle.studio.project.Project
import com.soyle.studio.workspace.WorkSpace

/**
 * Created by Brendan
 * Date: 2/11/2020
 * Time: 1:32 PM
 */
class ProjectClosed(val workspaceId: WorkSpace.Id, val projectId: Project.Id) : DomainEvent<WorkSpace.Id>(){
	override val aggregateId: WorkSpace.Id
		get() = workspaceId
}