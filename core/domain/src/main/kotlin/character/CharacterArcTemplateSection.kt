package com.soyle.stories.domain.character

import com.soyle.stories.domain.entities.Entity
import java.util.*

class CharacterArcTemplateSection(
    override val id: Id,
    val name: String,
    val isRequired: Boolean,
    val allowsMultiple: Boolean,
    val isMoral: Boolean
) : Entity<CharacterArcTemplateSection.Id> {

    data class Id(val uuid: UUID = UUID.randomUUID())

    override fun toString(): String {
        return "CharacterArcTemplateSection(id=${id.uuid}, name='$name', isRequired=$isRequired, allowsMultiple=$allowsMultiple, isMoral=$isMoral)"
    }


}