package com.soyle.stories.domain.character.events

import com.soyle.stories.domain.character.Character

class CharacterNameVariantAdded(characterId: Character.Id, val newVariant: String) : CharacterEvent(characterId)