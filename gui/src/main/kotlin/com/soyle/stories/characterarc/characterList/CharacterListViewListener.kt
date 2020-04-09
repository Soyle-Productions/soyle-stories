/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 4:03 PM
 */
package com.soyle.stories.characterarc.characterList

interface CharacterListViewListener {

    fun getList()
    fun openBaseStoryStructureTool(characterId: String, themeId: String)
    fun openCharacterComparison(characterId: String, themeId: String)
    fun removeCharacter(characterId: String)
    fun removeCharacterArc(characterId: String, themeId: String)
    fun renameCharacter(characterId: String, newName: String)

}