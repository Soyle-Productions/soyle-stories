package com.soyle.stories.usecase.scene.character.listAvailableCharacters

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import java.util.*

class AvailableCharactersToAddToScene(
    val sceneId: Scene.Id,
    availableCharacters: List<CharacterItem>
) : List<CharacterItem> by availableCharacters