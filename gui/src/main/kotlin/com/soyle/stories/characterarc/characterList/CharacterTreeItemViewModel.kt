/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 6:51 PM
 */
package com.soyle.stories.characterarc.characterList

data class CharacterTreeItemViewModel(val id: String, val name: String, val isExpanded: Boolean, val arcs: List<CharacterArcItemViewModel>) {
    override fun toString(): String = name
}