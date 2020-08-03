package com.soyle.stories.theme.characterConflict

interface CharacterConflictViewListener {

    fun getValidState(characterId: String?)
    fun getAvailableCharacters()
    fun getAvailableOpponents(characterId: String)
    fun addOpponent(perspectiveCharacterId: String, characterId: String)
    fun makeOpponentMainOpponent(perspectiveCharacterId: String, characterId: String)
    fun setCentralConflict(centralConflict: String)
    fun setDesire(characterId: String, desire: String)

}