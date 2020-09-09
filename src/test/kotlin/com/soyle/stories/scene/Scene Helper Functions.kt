package com.soyle.stories.scene

import com.soyle.stories.entities.CharacterInScene
import com.soyle.stories.entities.Scene

fun Scene.charactersInScene() = includedCharacters.map {
    getMotivationForCharacter(it.characterId)!!.let {
        CharacterInScene(it.characterId, id, it.characterName, it.motivation, listOf())
    }
}