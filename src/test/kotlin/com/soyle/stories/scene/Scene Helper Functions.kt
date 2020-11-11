package com.soyle.stories.scene

import com.soyle.stories.common.str
import com.soyle.stories.entities.*

fun makeScene(
    sceneId: Scene.Id = Scene.Id(),
    projectId: Project.Id = Project.Id(),
    name: String = "Scene ${str()}",
    storyEventId: StoryEvent.Id = StoryEvent.Id(),
    locationId: Location.Id? = null,
    charactersInScene: List<CharacterInScene> = listOf()
) = Scene(
    sceneId,
    projectId,
    name,
    storyEventId,
    locationId,
    charactersInScene
)

fun Scene.charactersInScene() = includedCharacters.map {
    getMotivationForCharacter(it.characterId)!!.let {
        CharacterInScene(it.characterId, id, it.characterName, it.motivation, listOf())
    }
}