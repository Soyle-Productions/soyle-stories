package com.soyle.studio.theme.entities

import com.soyle.studio.common.Entity
import com.soyle.studio.theme.valueobjects.CharacterArcSectionType
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:57 PM
 */
class CharacterArcSection(
	override val id: Id,
	val type: CharacterArcSectionType
) : Entity<CharacterArcSection.Id> {

	data class Id(val uniqueId: UUID)
}