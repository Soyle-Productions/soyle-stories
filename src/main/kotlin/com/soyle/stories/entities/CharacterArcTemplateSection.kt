package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import java.util.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 2:41 PM
 */
class CharacterArcTemplateSection(
    override val id: Id,
    val name: String,
    val isRequired: Boolean
) : Entity<CharacterArcTemplateSection.Id> {

    data class Id(val uuid: UUID)
}