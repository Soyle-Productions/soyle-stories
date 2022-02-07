package com.soyle.stories.usecase.scene.delete

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.CharacterInScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.SuccessfulStoryEventUpdate
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.effects.CharacterGainedInheritedMotivationInScene
import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import com.soyle.stories.usecase.scene.character.effects.InheritedCharacterMotivationInSceneCleared
import com.soyle.stories.usecase.scene.common.InheritedMotivation
import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingScene.OutputPort
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*

class GetPotentialChangesFromDeletingSceneUseCase(
    private val scenes: SceneRepository,
    locations: LocationRepository,
    private val storyEvents: StoryEventRepository,
    private val characters: CharacterRepository
) : GetPotentialChangesFromDeletingScene {

    private val deleteScene = DeleteSceneUseCase(
        scenes.preventUpdates(),
        locations.preventUpdates(),
        storyEvents.preventUpdates()
    )::invoke

    override suspend fun invoke(sceneId: Scene.Id, output: OutputPort) {
        deleteScene(sceneId) {
            output.receivePotentialChangesFromDeletingScene(
                PotentialChangesOfDeletingScene(
                    it.sceneRemoved,
                    it.storyEventsUncovered,
                    it.hostedScenesRemoved,
                    getInheritedMotivationChangesFromDeletingScene(sceneId)
                )
            )
        }
    }

    private suspend fun getInheritedMotivationChangesFromDeletingScene(sceneId: Scene.Id): List<CharacterInSceneEffect> {
        val scene = scenes.getSceneOrError(sceneId.uuid)

        return coroutineScope {
            val allScenes by lazyLoadScenesBeforeAndAfter(scene)
            val storyEventsByScene = storyEventsByScene()

            scene.includedCharacters
                .asFlow()
                .filter { it.motivation != null }
                .flatMapConcat { characterInScene ->
                    val (pastScenes, futureScenes) = allScenes.await()
                    getAffectedFutureScenesForCharacter(
                        futureScenes,
                        characterInScene,
                        storyEventsByScene,
                        inheritedMotivation(
                            scene,
                            characterInScene.id,
                            characterInScene.motivation!!
                        ),
                        getPossibleGainedMotivation(pastScenes, characterInScene)
                    )
                }
                .toList()
        }
    }

    private fun CoroutineScope.getAffectedFutureScenesForCharacter(
        futureScenes: List<Scene>,
        characterInScene: CharacterInScene,
        storyEventsByScene: (Scene.Id) -> Deferred<List<StoryEvent>>,
        lostMotivation: InheritedMotivation,
        possibleGainedMotivation: InheritedMotivation?
    ): Flow<CharacterInSceneEffect> {
        val backingCharacter by lazyAsync {
            characters.getCharacterOrError(characterInScene.id.uuid)
        }
        return futureScenes
            .asFlow()
            // take future scenes involving character until a new motivation is set
            .takeWhile {
                it.involvesCharacterWithoutOverridingMotivation(
                    characterInScene.id
                ) { storyEventsByScene(it.id).await() }
            }
            .map { futureScene ->
                inheritedMotivationEffect(
                    clearedFromScene = futureScene,
                    forCharacter = backingCharacter.await(),
                    lostMotivation = lostMotivation,
                    gainedMotivation = possibleGainedMotivation,
                )
            }
    }

    private fun getPossibleGainedMotivation(
        pastScenes: List<Scene>,
        characterInScene: CharacterInScene
    ) = pastScenes
        .asReversed()
        .find { it.includedCharacters[characterInScene.id]?.motivation != null }
        ?.let {
            inheritedMotivation(
                it,
                characterInScene.id,
                it.includedCharacters[characterInScene.id]!!.motivation!!
            )
        }


    private fun CoroutineScope.storyEventsByScene(): (Scene.Id) -> Deferred<List<StoryEvent>> {
        val storyEventsInScenes = mutableMapOf<Scene.Id, Deferred<List<StoryEvent>>>()
        return { id: Scene.Id ->
            storyEventsInScenes.getOrPut(id) {
                async { storyEvents.getStoryEventsCoveredByScene(id) }
            }
        }
    }

    private fun CoroutineScope.lazyLoadScenesBeforeAndAfter(scene: Scene) = lazyAsync {
        val sceneOrder = scenes.getSceneIdsInOrder(scene.projectId)!!.order.toList()
        val sceneIndex = sceneOrder.indexOf(scene.id)
        val past = sceneOrder
            .subList(0, sceneIndex)
            .map { scenes.getSceneOrError(it.uuid) }
        val future = sceneOrder
            .subList(sceneIndex + 1, sceneOrder.size)
            .map { scenes.getSceneOrError(it.uuid) }
        past to future
    }

    private suspend fun Scene.involvesCharacterWithoutOverridingMotivation(
        characterId: Character.Id,
        storyEventsInScene: suspend () -> List<StoryEvent>
    ): Boolean {
        return if (includesCharacter(characterId)) {
            includedCharacters[characterId]!!.motivation == null
        } else {
            storyEventsInScene().any { it.involvedCharacters.containsEntityWithId(characterId) }
        }
    }

    private fun inheritedMotivationEffect(
        clearedFromScene: Scene,
        forCharacter: Character,
        lostMotivation: InheritedMotivation,
        gainedMotivation: InheritedMotivation?,
    ) = if (gainedMotivation == null) {
        inheritedMotivationCleared(clearedFromScene, forCharacter, lostMotivation)
    } else inheritedMotivationGained(clearedFromScene, forCharacter, gainedMotivation, lostMotivation)

    private fun inheritedMotivationCleared(
        clearedFromScene: Scene,
        forCharacter: Character,
        lostMotivation: InheritedMotivation
    ) = InheritedCharacterMotivationInSceneCleared(
        clearedFromScene.id,
        clearedFromScene.name.value,
        forCharacter.id,
        forCharacter.displayName.value,
        lostMotivation
    )

    private fun inheritedMotivationGained(
        gainedInScene: Scene,
        forCharacter: Character,
        gainedMotivation: InheritedMotivation,
        lostMotivation: InheritedMotivation?
    ) = CharacterGainedInheritedMotivationInScene(
        gainedInScene.id,
        gainedInScene.name.value,
        forCharacter.id,
        forCharacter.displayName.value,
        gainedMotivation,
        lostMotivation
    )

    private fun inheritedMotivation(source: Scene, characterId: Character.Id, motivation: String) =
        InheritedMotivation(
            source.id,
            characterId,
            source.name.value,
            motivation
        )

    private fun SceneRepository.preventUpdates() = object : SceneRepository by this {
        override suspend fun updateSceneOrder(sceneOrder: SceneOrder) = Unit
        override suspend fun removeScene(sceneId: Scene.Id) = Unit
        override suspend fun updateScene(scene: Scene) = Unit
        override suspend fun updateScenes(scenes: List<Scene>): List<Unit> = listOf()
    }

    private fun LocationRepository.preventUpdates() = object : LocationRepository by this {
        override suspend fun addNewLocation(location: Location) = Unit
        override suspend fun removeLocation(location: Location) = Unit
        override suspend fun updateLocation(location: Location) = Unit
        override suspend fun updateLocations(locations: Set<Location>) = Unit
    }

    private fun StoryEventRepository.preventUpdates() = object : StoryEventRepository by this {
        override suspend fun addNewStoryEvent(storyEvent: StoryEvent) = Unit
        override suspend fun removeStoryEvent(storyEventId: StoryEvent.Id) = Unit
        override suspend fun save(update: SuccessfulStoryEventUpdate<*>): Throwable? = null
        override suspend fun updateStoryEvent(storyEvent: StoryEvent): Throwable? = null
        override suspend fun updateStoryEvents(vararg storyEvents: StoryEvent) = Unit
        override suspend fun trySave(storyEvent: StoryEvent): Boolean = true
    }

    private fun <T> CoroutineScope.lazyAsync(block: suspend CoroutineScope.() -> T) = lazy { async(block = block) }

}