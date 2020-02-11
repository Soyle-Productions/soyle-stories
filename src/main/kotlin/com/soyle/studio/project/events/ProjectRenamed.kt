package com.soyle.studio.project.events

import com.soyle.studio.common.DomainEvent
import com.soyle.studio.project.Project

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 3:15 PM
 */
data class ProjectRenamed(val projectId: Project.Id, val newName: String) : DomainEvent<Project.Id>() {
	override val aggregateId: Project.Id
		get() = projectId
}