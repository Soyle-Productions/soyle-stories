package com.soyle.studio.theme.events

import com.soyle.studio.common.DomainEvent
import com.soyle.studio.theme.Theme
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 4:02 PM
 */
data class ThemeCreated(
	val projectId: UUID,
	val themeId: Theme.Id
) : DomainEvent<Theme.Id>() {
	override val aggregateId: Theme.Id
		get() = themeId
}