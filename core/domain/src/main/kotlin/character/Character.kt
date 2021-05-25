package com.soyle.stories.domain.character

import com.soyle.stories.domain.character.events.CharacterNameVariantAdded
import com.soyle.stories.domain.character.events.CharacterNameVariantRemoved
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed
import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.entities.EntityUpdate
import com.soyle.stories.domain.media.Media
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class Character(
    override val id: Id,
    val projectId: Project.Id,
    val name: NonBlankString,
    val otherNames: Set<NonBlankString>,
    val media: Media.Id?
) : Entity<Character.Id> {

    constructor(projectId: Project.Id, name: NonBlankString, media: Media.Id? = null) : this(
        Id(),
        projectId,
        name,
        setOf(),
        media
    )

    private fun copy(
        name: NonBlankString = this.name,
        media: Media.Id? = this.media,
        otherNames: Set<NonBlankString> = this.otherNames
    ) = Character(id, projectId, name, otherNames, media)

    fun withName(name: NonBlankString): Character = copy(name = name)

    /*
    Should add the variant to the list of other names, unless it is the same as the name or one of the other names.
     */
    fun withNameVariant(variant: NonBlankString): CharacterUpdate<CharacterNameVariantAdded> {
        if (variant == name) return noUpdate(reason = CharacterNameVariantCannotEqualDisplayName(id, variant.value))
        if (variant in otherNames) return noUpdate(
            reason = CharacterNameVariantCannotEqualOtherVariant(
                id,
                variant.value
            )
        )
        return CharacterUpdate.Updated(
            character = copy(otherNames = otherNames + variant),
            event = CharacterNameVariantAdded(id, variant.value)
        )
    }

    fun withoutNameVariant(variant: NonBlankString): CharacterUpdate<CharacterNameVariantRemoved> {
        if (variant !in otherNames) return noUpdate(CharacterDoesNotHaveNameVariant(id, variant.value))
        return CharacterUpdate.Updated(
            character = copy(otherNames = otherNames - variant),
            event = CharacterNameVariantRemoved(id, variant)
        )
    }

    fun withNameVariantModified(
        currentVariant: NonBlankString,
        replacement: NonBlankString
    ): CharacterUpdate<CharacterNameVariantRenamed> {
        if (currentVariant == replacement) return noUpdate()
        if (currentVariant !in otherNames) return noUpdate(CharacterDoesNotHaveNameVariant(id, currentVariant.value))
        if (replacement == name) return noUpdate(
            reason = CharacterNameVariantCannotEqualDisplayName(
                id,
                replacement.value
            )
        )
        if (replacement in otherNames) return noUpdate(
            reason = CharacterNameVariantCannotEqualOtherVariant(
                id,
                replacement.value
            )
        )

        return CharacterUpdate.Updated(
            character = copy(otherNames = otherNames - currentVariant + replacement),
            event = CharacterNameVariantRenamed(id, currentVariant, replacement)
        )
    }

    fun noUpdate(reason: CharacterException? = null) = CharacterUpdate.WithoutChange(this, reason)

    data class Id(val uuid: UUID = UUID.randomUUID()) {

        override fun toString(): String = "Character($uuid)"
    }

    companion object {

        fun buildNewCharacter(projectId: Project.Id, name: NonBlankString): Character = Character(projectId, name)
    }
}

class CharacterRenamed(val characterId: Character.Id, val newName: String)