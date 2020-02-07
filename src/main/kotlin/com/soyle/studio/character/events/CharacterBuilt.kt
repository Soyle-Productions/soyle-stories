package com.soyle.studio.character.events

import com.soyle.studio.character.Character
import com.soyle.studio.common.DomainEvent
import java.util.*

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 8:27 PM
 */
data class CharacterBuilt(val projectId: UUID, override val aggregateId: Character.Id) : DomainEvent<Character.Id>()