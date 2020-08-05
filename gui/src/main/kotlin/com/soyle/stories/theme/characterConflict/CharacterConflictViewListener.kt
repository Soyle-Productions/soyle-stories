package com.soyle.stories.theme.characterConflict

interface CharacterConflictViewListener {

    fun getValidState(characterId: String?)
    fun getAvailableCharacters()
    fun getAvailableOpponents(characterId: String)
    fun addOpponent(perspectiveCharacterId: String, characterId: String)
    fun makeOpponentMainOpponent(perspectiveCharacterId: String, characterId: String)
    fun removeOpponent(perspectiveCharacterId: String, opponentId: String)
    fun setCentralConflict(centralConflict: String)
    fun setDesire(characterId: String, desire: String)
    fun setPsychologicalWeakness(characterId: String, weakness: String)
    fun setMoralWeakness(characterId: String, weakness: String)
    fun setCharacterChange(characterId: String, characterChange: String)
    fun setAttackFromOpponent(perspectiveCharacterId: String, opponentId: String, attack: String)
    fun setCharactersSimilarities(perspectiveCharacterId: String, opponentId: String, similarities: String)
    fun setCharacterAbilities(characterId: String, ability: String)

}