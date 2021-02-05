package com.soyle.stories.scene.usecases.includeCharacterInScene

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import java.util.*

class AvailableCharactersToAddToScene(
    val sceneId: UUID,
    availableCharacters: List<CharacterItem>
) : List<CharacterItem> by availableCharacters