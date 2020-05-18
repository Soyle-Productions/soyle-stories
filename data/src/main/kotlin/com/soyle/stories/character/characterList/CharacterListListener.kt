package com.soyle.stories.character.characterList

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem

interface CharacterListListener {

	fun receiveCharacterListUpdate(characters: List<CharacterItem>)

}