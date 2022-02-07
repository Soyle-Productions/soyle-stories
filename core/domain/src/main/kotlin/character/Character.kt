package com.soyle.stories.domain.character

import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.domain.character.exceptions.CharacterAlreadyRemovedFromStory
import com.soyle.stories.domain.character.exceptions.CharacterException
import com.soyle.stories.domain.character.name.CharacterNames
import com.soyle.stories.domain.character.name.events.CharacterDisplayNameSelected
import com.soyle.stories.domain.character.name.events.CharacterNameAdded
import com.soyle.stories.domain.character.name.events.CharacterNameRemoved
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.character.name.exceptions.CannotRemoveDisplayName
import com.soyle.stories.domain.character.name.exceptions.CharacterAlreadyHasName
import com.soyle.stories.domain.character.name.exceptions.CharacterDisplayNameAlreadySet
import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.media.Media
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class Character(
    override val id: Id,
    val projectId: Project.Id?,
    val names: CharacterNames,
    val media: Media.Id?
) : Entity<Character.Id> {

    constructor(projectId: Project.Id, name: NonBlankString, media: Media.Id? = null) : this(
        Id(),
        projectId,
        CharacterNames(name, emptySet()),
        media
    )

    val displayName: NonBlankString
        get() = names.displayName

    fun withName(newVariant: NonBlankString): CharacterUpdate<CharacterNameAdded> {
        if (newVariant in names) return noUpdate(CharacterAlreadyHasName(id, newVariant.value))
        return withEventApplied(CharacterNameAdded(id, newVariant.value))
    }

    fun removedFromStory(): CharacterUpdate<CharacterRemovedFromStory> {
        if (projectId == null) return noUpdate(CharacterAlreadyRemovedFromStory(id))
        return withEventApplied(CharacterRemovedFromStory(id, projectId))
    }

    interface NameOperations {
        fun asDisplayName(): CharacterUpdate<*>
        fun renamed(newName: NonBlankString): CharacterUpdate<CharacterRenamed>
        fun removed(): CharacterUpdate<CharacterNameRemoved>
    }
    fun withName(reference: String): NameOperations? {
        val nonBlankRef = NonBlankString.create(reference) ?: return null
        if (nonBlankRef !in names) return null
        return object : NameOperations {
            override fun renamed(newName: NonBlankString): CharacterUpdate<CharacterRenamed> {
                if (newName in names) return noUpdate(CharacterAlreadyHasName(id, newName.value))
                return withEventApplied(CharacterRenamed(id, nonBlankRef.value, newName.value))
            }

            override fun asDisplayName(): CharacterUpdate<*> {
                if (nonBlankRef == displayName) return noUpdate(CharacterDisplayNameAlreadySet(id, nonBlankRef.value))
                return withEventApplied(CharacterDisplayNameSelected(id, displayName.value, nonBlankRef.value))
            }

            override fun removed(): CharacterUpdate<CharacterNameRemoved> {
                if (displayName == nonBlankRef) return noUpdate(CannotRemoveDisplayName(id, nonBlankRef.value))
                return withEventApplied(CharacterNameRemoved(id, nonBlankRef.value))
            }
        }
    }

    fun noUpdate(reason: CharacterException? = null) = CharacterUpdate.WithoutChange(this, reason)

    private fun copy(
        projectId: Project.Id? = this.projectId,
        names: CharacterNames = this.names,
        media: Media.Id? = this.media
    ) = Character(
        id,
        projectId,
        names,
        media
    )

    private fun withEventApplied(event: CharacterNameAdded): CharacterUpdate.Updated<CharacterNameAdded>
    {
        return CharacterUpdate.Updated(
            copy(names = names.withName(NonBlankString.create(event.name)!!)),
            event
        )
    }

    private fun withEventApplied(event: CharacterNameRemoved): CharacterUpdate.Updated<CharacterNameRemoved>
    {
        return CharacterUpdate.Updated(
            copy(names = names.withoutName(NonBlankString.create(event.name)!!)),
            event
        )
    }

    private fun withEventApplied(event: CharacterRenamed): CharacterUpdate.Updated<CharacterRenamed>
    {
        return CharacterUpdate.Updated(
            copy(names = names.rename(NonBlankString.create(event.oldName)!!, NonBlankString.create(event.name)!!)),
            event
        )
    }

    private fun withEventApplied(event: CharacterDisplayNameSelected): CharacterUpdate.Updated<CharacterDisplayNameSelected>
    {
        return CharacterUpdate.Updated(
            copy(names = CharacterNames(NonBlankString.create(event.name)!!, names)),
            event
        )
    }

    private fun withEventApplied(event: CharacterRemovedFromStory): CharacterUpdate.Updated<CharacterRemovedFromStory>
    {
        return CharacterUpdate.Updated(
            copy(projectId = null),
            event
        )
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {

        override fun toString(): String = "Character($uuid)"
    }

    companion object {

        fun buildNewCharacter(projectId: Project.Id, name: NonBlankString): Character = Character(projectId, name)
    }
}