package com.soyle.stories.entities

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.common.Entity
import java.util.*

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 8:19 PM
 */
class Character(
    override val id: Id,
    val projectId: UUID,
    // Characters have a name
    val name: String
) : Entity<Character.Id> {

    init {
        if (name.isBlank()) throw com.soyle.stories.character.CharacterNameCannotBeBlank
    }

    fun rename(name: String): Either<com.soyle.stories.character.CharacterException, Character> {
        return try {
            Character(
                id,
                projectId,
                name
            ).right()
        } catch (e: com.soyle.stories.character.CharacterException) {
            e.left()
        }
    }

    data class Id(val uuid: UUID)

    companion object {
        fun buildNewCharacter(
            projectId: UUID,
            name: String
        ): Either<com.soyle.stories.character.CharacterException, Character> {
            return try {
                Character(
                    Id(UUID.randomUUID()),
                    projectId,
                    name
                ).right()
            } catch (e: com.soyle.stories.character.CharacterException) {
                e.left()
            }
        }
    }
}