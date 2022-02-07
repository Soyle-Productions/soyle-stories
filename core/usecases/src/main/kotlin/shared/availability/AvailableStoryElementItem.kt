package com.soyle.stories.usecase.shared.availability

import com.soyle.stories.domain.prose.MentionedEntityId

data class AvailableStoryElementItem<T : Any>(
    val entityId: MentionedEntityId<T>,
    val name: String,
    val parentEntityName: String?
)

typealias AnyAvailableStoryElementItem = AvailableStoryElementItem<*>