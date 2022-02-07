package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.character.CharacterInScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.str
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.noEntities

@Suppress("LongParameterList")
fun makeScene(
    sceneId: Scene.Id = Scene.Id(),
    projectId: Project.Id = Project.Id(),
    name: NonBlankString = sceneName(),
    settings: EntitySet<SceneSettingLocation> = noEntities(),
    proseId: Prose.Id = Prose.Id(),
    charactersInScene: EntitySet<CharacterInScene> = noEntities(),
    symbols: Collection<Scene.TrackedSymbol> = listOf(),
    conflict: SceneConflict = SceneConflict(""),
    resolution: SceneResolution = SceneResolution("")
): Scene = Scene(
    sceneId,
    projectId,
    name,
    settings,
    proseId,
    charactersInScene,
    symbols,
    conflict,
    resolution
)

fun sceneName(): NonBlankString = nonBlankStr("Scene ${str()}")

fun Scene.givenCharacter(character: Character): Scene = withCharacterIncluded(character).scene
fun Scene.givenCharacter(character: Character, motivation: String): Scene = withCharacterIncluded(character).scene
    .withCharacter(character.id)!!.motivationChanged(motivation).scene

fun Scene.givenCoveredSection(section: CharacterArcSection): Scene = withCharacterArcSectionCovered(section)