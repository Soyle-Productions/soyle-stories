package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.givenCharacter
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.character.effects.CharacterGainedInheritedMotivationInScene
import com.soyle.stories.usecase.scene.character.effects.InheritedCharacterMotivationInSceneCleared
import com.soyle.stories.usecase.scene.common.InheritedMotivation
import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingScene
import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingSceneUseCase
import com.soyle.stories.usecase.scene.delete.PotentialChangesOfDeletingScene
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Get Potential Changes from Deleting Scene Unit Test` {

    // Preconditions
    /** A project must have been started */
    val projectId = Project.Id()

    /** The scene must exist */
    private val scene = makeScene(projectId = projectId)

    // output
    private var result: PotentialChangesOfDeletingScene? = null

    // repositories
    private val sceneRepository = SceneRepositoryDouble(
        onRemoveScene = { error("should not remove scene") },
        onUpdateSceneOrder = { error("should not update scene order") }
    )
    private val locationRepository: LocationRepositoryDouble =
        LocationRepositoryDouble(onUpdateLocation = { error("should not update location") })
    private val storyEventRepository =
        StoryEventRepositoryDouble(onUpdateStoryEvent = { error("should not update story event") })
    private val characterRepository = CharacterRepositoryDouble()

    init {
        sceneRepository.givenScene(scene)
    }

    // use case
    val useCase: GetPotentialChangesFromDeletingScene = GetPotentialChangesFromDeletingSceneUseCase(
        sceneRepository,
        locationRepository,
        storyEventRepository,
        characterRepository
    )
    private fun getPotentialChanges() = runBlockingTest {
        useCase.invoke(scene.id) {
            result = it
        }
    }

    @Nested
    inner class `Scene Must Exist` {

        @Test
        fun `given scene does not exist - should output error`() {
            sceneRepository.scenes.remove(scene.id)

            val error = assertThrows<SceneDoesNotExist> { getPotentialChanges() }

            error.shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
        }

    }

    @Test
    fun `should output scene removed event`() {
        getPotentialChanges()

        result!!.sceneRemoved
    }

    @Test
    fun `should not actually remove scene`() {
        getPotentialChanges()

        sceneRepository.scenes.shouldContain(scene.id to scene)
    }

    @Test
    fun `given no locations host scene - should not output location events`() {
        getPotentialChanges()

        result!!.hostedScenesRemoved.shouldBeEmpty()
    }

    @Nested
    inner class `Given Locations Host Scene` {

        private val locations =
            List(5) { makeLocation(projectId = projectId).withSceneHosted(scene.id, scene.name.value).location }
                .onEach(locationRepository::givenLocation)

        init {
            locations.fold(scene) { nextScene, location -> nextScene.withLocationLinked(location).scene }
                .let(sceneRepository::givenScene)
        }

        @Test
        fun `should output hosted scenes removed events`() {
            getPotentialChanges()

            with(result!!) {
                hostedScenesRemoved.map { it.locationId }.toSet().mustEqual(locations.map { it.id }.toSet())
                hostedScenesRemoved.forEach { it.sceneId.mustEqual(scene.id) }
            }
        }

        @Test
        fun `should not update locations`() {
            getPotentialChanges()

            locationRepository.locations.shouldContainAll(locations.associateBy { it.id })
        }

    }

    @Test
    fun `given no story events covered - should not output story event changes`() {
        getPotentialChanges()

        result!!.storyEventsUncovered.shouldBeEmpty()
    }

    @Nested
    inner class `Given Story Events are Covered by Scene` {

        private val storyEvent = makeStoryEvent(sceneId = scene.id)

        init {
            storyEventRepository.givenStoryEvent(storyEvent)
        }

        @Test
        fun `should produce story event uncovered event`() {
            getPotentialChanges()

            with(result!!) {
                storyEventsUncovered.single()
                    .shouldBeEqualTo(StoryEventUncoveredFromScene(storyEvent.id, scene.id))
            }
        }

    }

    @Test
    fun `given no dependent scenes - should not output any inherited motivation changes`() {
        getPotentialChanges()

        result!!.inheritedCharacterMotivationChanges.shouldBeEmpty()
    }

    @Nested
    inner class `May output inherited motivation changes` {

        private val characters = List(4) { makeCharacter() }
            .onEach(characterRepository::givenCharacter)

        private val scene =
            characters.fold(this@`Get Potential Changes from Deleting Scene Unit Test`.scene) { nextScene, character ->
                nextScene.withCharacterIncluded(character).scene
                    .withCharacter(character.id)!!.motivationChanged("Some Motivation").scene
            }
                .also(sceneRepository::givenScene)

        @Nested
        inner class `Scene Must include Characters` {

            @Test
            fun `given not characters in included - should not output any inherited motivation changes`() {
                characters.fold(scene) { nextScene, character ->
                    nextScene.withCharacter(character.id)!!.removed().scene
                }
                    .also(sceneRepository::givenScene)

                getPotentialChanges()

                result!!.inheritedCharacterMotivationChanges.shouldBeEmpty()
            }

        }

        @Nested
        inner class `Output Characters must Have Motivation in Scene` {

            @Test
            fun `given characters in scene do not have motivations - should not output any inherited motivation changes`() {
                characters.fold(scene) { nextScene, character ->
                    nextScene.withCharacter(character.id)!!.motivationChanged(null).scene
                }.also(sceneRepository::givenScene)

                getPotentialChanges()

                result!!.inheritedCharacterMotivationChanges.shouldBeEmpty()
            }

        }

        @Test
        fun `should output inherited motivation change for character in future scene`() {
            val character = makeCharacter().also(characterRepository::givenCharacter)

            val futureScene = makeScene(projectId = projectId).withCharacterIncluded(character)
                .scene.also(sceneRepository::givenScene)

            scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.motivationChanged("Get dat bread")
                .scene.also(sceneRepository::givenScene)


            getPotentialChanges()


            result!!.inheritedCharacterMotivationChanges.shouldContain(
                inheritedMotivationCleared(from = futureScene, `for` = character, motiveWas = "Get dat bread")
            )
        }

        @Test
        fun `Scenes before removal scene are not affected`() {
            val character = makeCharacter().also(characterRepository::givenCharacter)

            val futureScene = makeScene(projectId = projectId).withCharacterIncluded(character)
                .scene.also(sceneRepository::givenScene)

            scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.motivationChanged("Get dat bread")
                .scene.also(sceneRepository::givenScene)

            sceneRepository.sceneOrders[scene.projectId] =
                SceneOrder.reInstantiate(scene.projectId, listOf(futureScene.id, scene.id))


            getPotentialChanges()


            result!!.inheritedCharacterMotivationChanges.shouldBeEmpty()
        }

        @Test
        fun `character must have motivation in removal scene to affect future scenes`() {
            val characters = List(3) { makeCharacter() }.onEach(characterRepository::givenCharacter)
            val futureScene = makeScene(projectId = projectId)
                .withCharacterIncluded(characters[0]).scene
                .withCharacterIncluded(characters[1]).scene
                .withCharacterIncluded(characters[2]).scene
                .also(sceneRepository::givenScene)
            scene
                .withCharacterIncluded(characters[0]).scene
                .withCharacter(characters[0].id)!!.motivationChanged("Motivation for first character").scene
                .withCharacterIncluded(characters[1]).scene
                .withCharacterIncluded(characters[2]).scene
                .withCharacter(characters[2].id)!!.motivationChanged("Motivation for third character").scene
                .also(sceneRepository::givenScene)

            getPotentialChanges()

            result!!.inheritedCharacterMotivationChanges.shouldHaveSize(2)
            result!!.inheritedCharacterMotivationChanges.map { it.character }.toSet()
                .shouldBeEqualTo(setOf(characters[0].id, characters[2].id))
        }

        @Test
        fun `A future scene with a motivation for character should prevent even further future scenes from being affected`() {
            val (sceneA, sceneB, sceneC, sceneD, sceneE) = List(5) { makeScene(projectId = scene.projectId) }
            sceneRepository.givenOrder(scene, sceneA, sceneB, sceneC, sceneD, sceneE)
            val character = makeCharacter().also(characterRepository::givenCharacter)
            listOf(
                scene.givenCharacter(character, motivation = "First Motive"),
                sceneA.givenCharacter(character),
                sceneB.givenCharacter(character),
                sceneC.givenCharacter(character, motivation = "Second Motive"),
                sceneD.givenCharacter(character),
                sceneE.givenCharacter(character),
            ).onEach(sceneRepository::givenScene)

            getPotentialChanges()

            result!!.inheritedCharacterMotivationChanges.shouldHaveSize(2)
            result!!.inheritedCharacterMotivationChanges.shouldContain(
                inheritedMotivationCleared(from = sceneA, `for` = character, motiveWas = "First Motive")
            )
            result!!.inheritedCharacterMotivationChanges.shouldContain(
                inheritedMotivationCleared(from = sceneB, `for` = character, motiveWas = "First Motive")
            )
        }

        @Test
        fun `character may be involved in story event without being explicitly included in future scene`() {
            val character = makeCharacter()
                .also(characterRepository::givenCharacter)
            val futureScene = makeScene(projectId = projectId).also(sceneRepository::givenScene)
            makeStoryEvent(sceneId = futureScene.id, projectId = projectId)
                .withCharacterInvolved(character).storyEvent
                .also(storyEventRepository::givenStoryEvent)
            scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.motivationChanged("Get dat bread")
                .scene.also(sceneRepository::givenScene)

            getPotentialChanges()

            result!!.inheritedCharacterMotivationChanges.shouldContain(
                inheritedMotivationCleared(from = futureScene, `for` = character, motiveWas = "Get dat bread")
            )
        }

        @Test
        fun `past scenes may provide a new inherited motivation for future scenes`() {
            val (sceneA, sceneB) = List(2) { makeScene(projectId = projectId) }
            sceneRepository.givenOrder(sceneA, scene, sceneB)
            val character = makeCharacter().also(characterRepository::givenCharacter)
            listOf(
                sceneA.givenCharacter(character, motivation = "Past Motivation"),
                scene.givenCharacter(character, motivation = "Current Motivation"),
                sceneB.givenCharacter(character)
            ).onEach(sceneRepository::givenScene)

            getPotentialChanges()

            result!!.inheritedCharacterMotivationChanges.shouldContain(
                inheritedMotivationOverridden(
                    on = sceneB,
                    `for` = character,
                    motiveWas = "Current Motivation",
                    motiveNow = "Past Motivation",
                    newSource = sceneA
                )
            )

        }

    }


    private fun inheritedMotivationCleared(from: Scene, `for`: Character, motiveWas: String) =
        InheritedCharacterMotivationInSceneCleared(
            from.id,
            from.name.value,
            `for`.id,
            `for`.displayName.value,
            InheritedMotivation(
                scene.id,
                `for`.id,
                scene.name.value,
                motiveWas
            )
        )

    private fun inheritedMotivationOverridden(
        on: Scene,
        `for`: Character,
        motiveWas: String,
        motiveNow: String,
        newSource: Scene
    ) = CharacterGainedInheritedMotivationInScene(
        on.id,
        on.name.value,
        `for`.id,
        `for`.displayName.value,
        InheritedMotivation(
            newSource.id,
            `for`.id,
            newSource.name.value,
            motiveNow
        ),
        InheritedMotivation(
            scene.id,
            `for`.id,
            scene.name.value,
            motiveWas
        )
    )

    /*

    May output inherited motivation changes

        future scenes must have the same character that inherit from this scene

            scenes may include the characters explicitly

            scenes may cover story events with characters involved

    May output new inherited motivations

        past scenes must have the same characters and provide motivations

            future scenes must have the same character that inherit from this scene

                scenes may include the characters explicitly

                scenes may cover story events with characters involved

     */
}