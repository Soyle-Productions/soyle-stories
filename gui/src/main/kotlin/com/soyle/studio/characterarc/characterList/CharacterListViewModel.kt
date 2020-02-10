package com.soyle.studio.characterarc.characterList

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:01 PM
 */
class CharacterListViewModel(
    val characters: List<CharacterItemViewModel>
)

class CharacterItemViewModel(val id: String, val name: String, val arcs: List<CharacterArcItemViewModel>) {
    override fun toString(): String = name
}
class CharacterArcItemViewModel(val themeId: String, val name: String) {
    override fun toString(): String = name
}