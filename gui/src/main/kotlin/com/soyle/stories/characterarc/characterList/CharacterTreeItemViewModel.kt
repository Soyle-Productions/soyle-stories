package com.soyle.stories.characterarc.characterList

data class CharacterTreeItemViewModel(
    val id: String,
    val name: String,
    val imageResource: String,
    val isExpanded: Boolean,
    val arcs: List<CharacterArcItemViewModel>
)