package com.soyle.stories.theme.characterValueComparison

interface CharacterValueComparisonViewListener {

    fun getValidState()
    fun openValueWebTool(themeId: String)
    fun getAvailableCharacters()
    fun addCharacter(characterId: String)
    fun removeCharacter(characterId: String)
    fun setCharacterArchetype(characterId: String, archetype: String)
    fun getAvailableOppositionValues(characterId: String)
    fun selectOppositionValueForCharacter(characterId: String, oppositionValueId: String)
    fun removeOppositionValueFromCharacter(characterId: String, oppositionValueId: String)

}