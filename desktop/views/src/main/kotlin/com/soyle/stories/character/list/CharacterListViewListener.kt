package com.soyle.stories.character.list

import com.soyle.stories.domain.validation.NonBlankString

interface CharacterListViewListener {

    fun getList()
    fun openBaseStoryStructureTool(characterId: String, themeId: String)
    fun openCharacterValueComparison(themeId: String)
    fun openCentralConflict(themeId: String, characterId: String)
    fun removeCharacter(characterId: String)
    fun removeCharacterArc(characterId: String, themeId: String)
    fun renameCharacter(characterId: String, newName: NonBlankString)
    fun renameCharacterArc(characterId: String, themeId: String, newName: NonBlankString)

}