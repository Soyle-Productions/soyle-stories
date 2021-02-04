package com.soyle.stories.scene

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.str
import com.soyle.stories.entities.*

fun makeScene(
    sceneId: Scene.Id = Scene.Id(),
    projectId: Project.Id = Project.Id(),
    name: NonBlankString = NonBlankString.create("Scene ${str()}")!!,
    storyEventId: StoryEvent.Id = StoryEvent.Id(),
    settings: Set<Location.Id> = emptySet(),
    proseId: Prose.Id = Prose.Id(),
    charactersInScene: List<CharacterInScene> = listOf(),
    symbols: Collection<Scene.TrackedSymbol> = listOf()
) = Scene(
    sceneId,
    projectId,
    name,
    storyEventId,
    settings,
    proseId,
    charactersInScene,
    symbols
)

fun Scene.charactersInScene() = includedCharacters.map {
    getMotivationForCharacter(it.characterId)!!.let {
        CharacterInScene(it.characterId, id, it.characterName, it.motivation, listOf())
    }
}