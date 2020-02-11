package com.soyle.studio.workspace.events

import com.soyle.studio.common.DomainEvent
import com.soyle.studio.project.Project
import com.soyle.studio.workspace.WorkSpace

/**
 * Created by Brendan
 * Date: 2/11/2020
 * Time: 1:30 PM
 */
class ProjectOpened(val workspaceId: WorkSpace.Id, val projectId: Project.Id) : DomainEvent<WorkSpace.Id>(){
	override val aggregateId: WorkSpace.Id
		get() = workspaceId
}