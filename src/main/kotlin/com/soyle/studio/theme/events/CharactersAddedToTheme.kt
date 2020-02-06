package com.soyle.studio.theme.events

import com.soyle.studio.common.DomainEvent
import com.soyle.studio.theme.Theme
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 4:01 PM
 */
data class CharactersAddedToTheme(val themeId: Theme.Id, val characters: Set<UUID>) : DomainEvent<Theme.Id>() {
	override val aggregateId: Theme.Id
		get() = themeId
}