package com.soyle.studio.project.events

import com.soyle.studio.common.DomainEvent
import com.soyle.studio.project.Project

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 2:22 PM
 */
data class ProjectStarted(val projectId: Project.Id) : DomainEvent<Project.Id>() {
	override val aggregateId: Project.Id
		get() = projectId
}