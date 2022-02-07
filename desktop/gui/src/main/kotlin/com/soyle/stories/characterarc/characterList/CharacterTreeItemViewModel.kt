package com.soyle.stories.characterarc.characterList

import com.soyle.stories.domain.character.Character

data class CharacterTreeItemViewModel(
    val id: String,
    val name: String,
    val imageResource: String,
    val isExpanded: Boolean,
    val arcs: List<CharacterArcItemViewModel>
)

data class CharacterItemViewModel(
    val characterId: Character.Id,
    val characterName: String,
    val imageResource: String
)