package com.soyle.stories.usecase.scene.includeCharacterInScene

import com.soyle.stories.usecase.character.listAllCharacterArcs.CharacterItem
import java.util.*

class AvailableCharactersToAddToScene(
    val sceneId: UUID,
    availableCharacters: List<CharacterItem>
) : List<CharacterItem> by availableCharacters