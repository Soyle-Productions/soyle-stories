package com.soyle.stories.domain.scene

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.str
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.noEntities

fun makeScene(
    sceneId: Scene.Id = Scene.Id(),
    projectId: Project.Id = Project.Id(),
    name: NonBlankString = nonBlankStr("Scene ${str()}"),
    storyEventId: StoryEvent.Id = StoryEvent.Id(),
    settings: EntitySet<SceneSettingLocation> = noEntities(),
    proseId: Prose.Id = Prose.Id(),
    charactersInScene: List<CharacterInScene> = listOf(),
    symbols: Collection<Scene.TrackedSymbol> = listOf(),
    conflict: SceneConflict = SceneConflict(""),
    resolution: SceneResolution = SceneResolution("")
) = Scene(
    sceneId,
    projectId,
    name,
    storyEventId,
    settings,
    proseId,
    charactersInScene,
    symbols,
    conflict,
    resolution
)