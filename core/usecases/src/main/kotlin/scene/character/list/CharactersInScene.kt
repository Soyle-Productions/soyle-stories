package com.soyle.stories.usecase.scene.character.list

import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.usecase.scene.character.involve.CharacterInvolvedInScene

data class CharactersInScene(
    val sceneId: Scene.Id,
    val sceneName: String,
    val items: List<CharacterInSceneItem>,
) {

    private val coveredStoryEventIds by lazy { items.flatMapTo(LinkedHashSet()) { it.sourceIds } }

    fun withEventApplied(event: SceneRenamed): CharactersInScene {
        if (event.sceneId != sceneId) return this
        return copy(sceneName = event.sceneName)
    }

    fun withEventApplied(event: CharacterIncludedInScene): CharactersInScene {
        if (event.sceneId != sceneId) return this
        val newItem = CharacterInSceneItem(
            event.characterId,
            event.sceneId,
            event.projectId,
            event.characterName,
            true,
            null,
            setOf(),
        )
        return copy(items = items + newItem)
    }

    fun withEventApplied(event: CharacterInvolvedInScene): CharactersInScene {
        if (event.scene != sceneId) return this
        val newItem = CharacterInSceneItem(
            event.character,
            event.scene,
            event.project,
            event.characterName,
            false,
            null,
            setOf(event.source),
        )
        return copy(items = items + newItem)
    }

    private fun Sequence<CharacterInSceneItem>.filterStillInScene() = filter {
        it.isExplicit || (it.sources.isNotEmpty() && it.project != null)
    }

    fun withEventApplied(event: CharacterRemovedFromStory): CharactersInScene {
        if (items.none { it.characterId == event.characterId }) return this
        return copy(items = items.asSequence()
            .map {
                if (it.characterId == event.characterId) it.copy(project = null)
                else it
            }
            .filterStillInScene()
            .toList()
        )
    }

    fun withEventApplied(event: CharacterRemovedFromScene): CharactersInScene {
        if (event.sceneId != sceneId) return this
        return copy(items = items.asSequence()
            .map {
                if (it.characterId == event.characterId) it.copy(isExplicit = false)
                else it
            }
            .filterStillInScene()
            .toList()
        )
    }

    fun withEventApplied(event: StoryEventUncoveredFromScene): CharactersInScene {
        if (event.previousSceneId != sceneId) return this
        return copy(items = items.asSequence()
            .map { item ->
                item.copy(sources = item.sources.filterNotTo(LinkedHashSet()) { it.storyEvent == event.storyEventId })
            }
            .filterStillInScene()
            .toList()
        )
    }

    fun withEventApplied(event: CharacterRemovedFromStoryEvent): CharactersInScene {
        if (event.storyEventId !in coveredStoryEventIds) return this
        return copy(items = items.asSequence()
            .map { item ->
                if (item.characterId == event.characterId) {
                    item.copy(sources = item.sources.filterNotTo(LinkedHashSet()) { it.storyEvent == event.storyEventId })
                } else item
            }
            .filterStillInScene()
            .toList()
        )
    }

    fun withEventApplied(event: StoryEventNoLongerHappens): CharactersInScene {
        if (event.storyEventId !in coveredStoryEventIds) return this
        return copy(items = items.asSequence()
            .map { item ->
                item.copy(sources = item.sources.filterNotTo(LinkedHashSet()) { it.storyEvent == event.storyEventId })
            }
            .filterStillInScene()
            .toList()
        )
    }

}