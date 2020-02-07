package com.soyle.studio.character.events

import com.soyle.studio.character.Character
import com.soyle.studio.common.DomainEvent

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 8:39 PM
 */
data class CharacterRenamed(override val aggregateId: Character.Id, val name: String) : DomainEvent<Character.Id>()