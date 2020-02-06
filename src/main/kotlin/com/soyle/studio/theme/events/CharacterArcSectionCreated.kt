package com.soyle.studio.theme.events

import com.soyle.studio.common.DomainEvent
import com.soyle.studio.theme.Theme
import com.soyle.studio.theme.entities.CharacterArcSection
import com.soyle.studio.theme.valueobjects.CharacterArcSectionType
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 5:17 PM
 */
data class CharacterArcSectionCreated(val themeId: Theme.Id, val characterId: UUID, val arcSectionId: CharacterArcSection.Id, val type: CharacterArcSectionType) : DomainEvent<Theme.Id>() {
	override val aggregateId: Theme.Id
		get() = themeId
}