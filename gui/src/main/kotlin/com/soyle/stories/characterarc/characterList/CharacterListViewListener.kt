package com.soyle.stories.characterarc.characterList

interface CharacterListViewListener {

    fun getList()
    fun openBaseStoryStructureTool(characterId: String, themeId: String)
    fun openCharacterValueComparison(themeId: String)
    fun openCentralConflict(themeId: String, characterId: String)
    fun removeCharacter(characterId: String)
    fun removeCharacterArc(characterId: String, themeId: String)
    fun renameCharacter(characterId: String, newName: String)
    fun renameCharacterArc(characterId: String, themeId: String, newName: String)

}