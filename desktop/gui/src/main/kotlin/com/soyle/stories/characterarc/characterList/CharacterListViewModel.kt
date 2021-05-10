package com.soyle.stories.characterarc.characterList

data class CharacterListViewModel(
    val characters: List<CharacterListItemViewModel>
)

data class CharacterListItemViewModel(
    val item: CharacterItemViewModel,
    val arcs: List<CharacterArcItemViewModel>
)