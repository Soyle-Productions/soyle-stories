/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 6:51 PM
 */
package com.soyle.stories.characterarc.characterList

class CharacterArcItemViewModel(val characterId: String, val themeId: String, val name: String) {
    override fun toString(): String = name
}