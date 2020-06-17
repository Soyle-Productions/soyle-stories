package com.soyle.stories.scene

import com.soyle.stories.entities.Scene

fun Scene.characterMotivations() = includedCharacters.map { getMotivationForCharacter(it.characterId)!! }