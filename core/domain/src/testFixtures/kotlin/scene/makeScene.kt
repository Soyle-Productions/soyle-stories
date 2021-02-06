package com.soyle.stories.domain.scene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.str
import com.soyle.stories.domain.validation.NonBlankString

fun makeScene(
    sceneId: Scene.Id = Scene.Id(),
    projectId: Project.Id = Project.Id(),
    name: NonBlankString = nonBlankStr("Scene ${str()}"),
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