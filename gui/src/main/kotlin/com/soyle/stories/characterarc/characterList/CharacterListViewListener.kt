package com.soyle.stories.characterarc.characterList

import com.soyle.stories.common.NonBlankString

interface CharacterListViewListener {

    fun getList()
    fun openBaseStoryStructureTool(characterId: String, themeId: String)
    fun openCharacterValueComparison(themeId: String)
    fun openCentralConflict(themeId: String, characterId: String)
    fun removeCharacter(characterId: String)
    fun removeCharacterArc(characterId: String, themeId: String)
    fun renameCharacter(characterId: String, newName: NonBlankString)
    fun renameCharacterArc(characterId: String, themeId: String, newName: String)

}