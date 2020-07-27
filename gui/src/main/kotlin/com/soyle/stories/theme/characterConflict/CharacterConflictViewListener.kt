package com.soyle.stories.theme.characterConflict

interface CharacterConflictViewListener {

    fun getValidState(characterId: String?)
    fun getAvailableCharacters()

}