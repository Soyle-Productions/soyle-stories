package com.soyle.studio.workspace

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.studio.common.AggregateRoot
import com.soyle.studio.common.DomainEvent
import com.soyle.studio.project.Project
import com.soyle.studio.workspace.events.ProjectClosed
import com.soyle.studio.workspace.events.ProjectOpened
import java.net.URI
import java.util.*

/**
 * Created by Brendan
 * Date: 2/11/2020
 * Time: 1:11 PM
 */
class WorkSpace private constructor(
	override val id: Id,
	val openProjects: List<OpenProject>,
	override val events: List<DomainEvent<Id>>
) : AggregateRoot<WorkSpace.Id>
{

	constructor(id: Id, openProjects: List<OpenProject>) : this(id, openProjects, emptyList())

	fun addProject(projectId: Project.Id, projectName: String, projectURI: URI): Either<ProjectAlreadyOpen, WorkSpace>
	{
		val openProject = openProjects.find { it.projectId == projectId }
		if (openProject != null) {
			return ProjectAlreadyOpen(id, projectId).left()
		}
		return WorkSpace(id, openProjects + OpenProject(projectId, projectName, projectURI), events + ProjectOpened(id, projectId)).right()
	}

	fun closeProject(projectId: Project.Id): Either<Nothing, WorkSpace>
	{
		val openProject = openProjects.find { it.projectId == projectId } ?: return this.right()
		return WorkSpace(id, openProjects - openProject, events + ProjectClosed(id, projectId)).right()
	}

	class Id(val uuid: UUID)

	companion object {
		fun createNewWorkSpace(): Either<*, WorkSpace>
		{
			return WorkSpace(Id(UUID.randomUUID()), emptyList()).right()
		}
	}
}