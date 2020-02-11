package com.soyle.studio.character

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.studio.character.events.CharacterBuilt
import com.soyle.studio.character.events.CharacterRenamed
import com.soyle.studio.common.AggregateRoot
import com.soyle.studio.common.DomainEvent
import java.util.*

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 8:19 PM
 */
class Character private constructor(
	override val id: Id,
	val projectId: UUID,
	val name: String,
	override val events: List<DomainEvent<Id>>
) : AggregateRoot<Character.Id> {

	constructor(
		id: Id,
		projectId: UUID,
		name: String
	) : this(id, projectId, name, listOf())

	fun rename(name: String): Either<*, Character> {
		if (name.isBlank()) return CharacterNameCannotBeBlank.left()
		return Character(id, projectId, name, events + CharacterRenamed(id, name)).right()
	}

	data class Id(val uuid: UUID)

	companion object {
		fun buildNewCharacter(projectId: UUID, name: String): Either<*, Character> {
			if (name.isBlank()) return CharacterNameCannotBeBlank.left()
			val characterId = Id(UUID.randomUUID())
			return Either.Right(Character(characterId, projectId, name, listOf(CharacterBuilt(projectId, characterId))))
		}
	}
}