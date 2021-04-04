package com.soyle.stories.usecase.scene.includedCharacter.listAvailableCharacters

import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import java.util.*

class AvailableCharactersToAddToScene(
    val sceneId: UUID,
    availableCharacters: List<CharacterItem>
) : List<CharacterItem> by availableCharacters