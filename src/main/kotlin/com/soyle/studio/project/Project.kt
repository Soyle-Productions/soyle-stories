package com.soyle.studio.project

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.studio.common.AggregateRoot
import com.soyle.studio.common.DomainEvent
import com.soyle.studio.project.events.ProjectRenamed
import com.soyle.studio.project.events.ProjectStarted
import java.net.URI
import java.net.URISyntaxException
import java.util.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 2:17 PM
 */
class Project private constructor(
	override val id: Id,
	val uri: URI,
	val name: String,
	override val events: List<DomainEvent<Id>>
) : AggregateRoot<Project.Id> {

	constructor(id: Id, uri: URI, name: String) : this(id, uri, name, emptyList())

	class Id(val uuid: UUID)

	fun rename(newName: String): Either<*, Project>
	{
		if (newName.isBlank()) return NameCannotBeBlank.left()
		return Project(id, uri, newName, events + ProjectRenamed(id, newName)).right()
	}

	companion object {
		fun startNew(name: String, uri: String): Either<*, Project>
		{
			if (name.isBlank()) return NameCannotBeBlank.left()
			val projectURI = try {
				URI(uri)
			} catch (e: URISyntaxException) {
				return e.left()
			}
			val projectId = Id(UUID.randomUUID())
			return Project(
				projectId,
				projectURI,
				name,
				listOf(ProjectStarted(projectId))
			).right()
		}
	}

}