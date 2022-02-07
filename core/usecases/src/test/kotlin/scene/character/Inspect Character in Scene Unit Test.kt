package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.givenCharacter
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.inspect.InspectCharacterInScene
import com.soyle.stories.usecase.scene.character.inspect.InspectCharacterInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class `Inspect Character in Scene Unit Test` {

    // Pre-Conditions

    /** The [character] must exist */
    private val character = makeCharacter()

    /** The [scene] must exist */
    private val scene = makeScene().withCharacterIncluded(character).scene

    // Post-Conditions

    /** The [scene] must be updated to include the [character] */
    private var updatedScene: Scene? = null

    // Wiring

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
        .apply { givenScene(scene) }
    private val characterRepository = CharacterRepositoryDouble()
        .apply { givenCharacter(character) }
    private val storyEventRepository = StoryEventRepositoryDouble()

    // Tests

    @Nested
    inner class `Scene Must Exist` {
        init {
            sceneRepository.scenes.remove(scene.id)
        }

        @Test
        fun `should return error`() {
            val result = inspectCharacterInScene()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
        }

    }

    @Nested
    inner class `Scene Must Include Character Implicitly or Explicitly` {

        init {
            sceneRepository.givenScene(scene.withCharacter(character.id)!!.removed().scene)
        }

        @Test
        fun `should return error`() {
            val result = inspectCharacterInScene()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(SceneDoesNotIncludeCharacter(scene.id, character.id))
        }

    }

    @Nested
    inner class `Character Must Exist` {

        init {
            characterRepository.characters.remove(character.id)
        }

        @Test
        fun `should return error`() {
            val result = inspectCharacterInScene()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(CharacterDoesNotExist(character.id))
        }

    }

    @Test
    fun `should output character name`() {
        val inspection = inspectCharacterInScene().getOrThrow()

        inspection.characterId.shouldBeEqualTo(character.id)
        inspection.characterName.shouldBeEqualTo(character.displayName.value)
        inspection.scene.shouldBeEqualTo(scene.id)
        inspection.project.shouldBeEqualTo(character.projectId)
    }

    @Nested
    inner class `Should output if Character is Explicitly Included` {

        @Test
        fun `if only included via story event - should output implicit`() {
            sceneRepository.givenScene(scene.withCharacter(character.id)!!.removed().scene)
            makeStoryEvent(sceneId = scene.id).withCharacterInvolved(character)
                .storyEvent.let(storyEventRepository::givenStoryEvent)

            val inspection = inspectCharacterInScene().getOrThrow()

            inspection.isExplicit.shouldBeFalse()
        }

        @Test
        fun `if included explicitly - should output explicit`() {
            val inspection = inspectCharacterInScene().getOrThrow()

            inspection.isExplicit.shouldBeTrue()
        }

    }

    @TestFactory
    fun `should output character role`() = listOf(

        dynamicTest("No Role") {
            val inspection = inspectCharacterInScene().getOrThrow()
            inspection.roleInScene.shouldBeNull()
        },

        dynamicTest("Inciting Character") {
            scene.withCharacter(character.id)!!.assignedRole(RoleInScene.IncitingCharacter)
                .scene.let(sceneRepository::givenScene)

            val inspection = inspectCharacterInScene().getOrThrow()
            inspection.roleInScene.shouldBeEqualTo(RoleInScene.IncitingCharacter)
        },

        dynamicTest("Opponent Character") {
            scene.withCharacter(character.id)!!.assignedRole(RoleInScene.OpponentCharacter)
                .scene.let(sceneRepository::givenScene)

            val inspection = inspectCharacterInScene().getOrThrow()
            inspection.roleInScene.shouldBeEqualTo(RoleInScene.OpponentCharacter)
        },

        dynamicTest("Implicitly Included") {
            sceneRepository.givenScene(scene.withCharacter(character.id)!!.removed().scene)
            makeStoryEvent(sceneId = scene.id).withCharacterInvolved(character)
                .storyEvent.let(storyEventRepository::givenStoryEvent)

            val inspection = inspectCharacterInScene().getOrThrow()
            inspection.roleInScene.shouldBeNull()
        }
    )

    @Nested
    inner class `Should output Source Story Events` {

        @Test
        fun `explicitly included with no story events - should output empty sources`() {
            val inspection = inspectCharacterInScene().getOrThrow()

            inspection.sources.shouldBeEmpty()
        }

        @TestFactory
        fun `covered story events involve character - should output all sources`(): List<DynamicNode> {
            val expectedSources = List(5) {
                makeStoryEvent(sceneId = scene.id).withCharacterInvolved(character)
                    .storyEvent
            }.onEach(storyEventRepository::givenStoryEvent)
                .associateBy { it.id }

            fun CharacterInSceneInspection.assertHasAllSources() {
                sources.shouldHaveSize(5)
                sources.forEach {
                    val backingSource = expectedSources.getValue(it.storyEvent)
                    it.name.shouldBeEqualTo(backingSource.name.value)
                }
            }

            return listOf(
                dynamicTest("explicitly included") {
                    val inspection = inspectCharacterInScene().getOrThrow()

                    inspection.assertHasAllSources()
                },
                dynamicTest("implicitly included") {
                    sceneRepository.givenScene(scene.withCharacter(character.id)!!.removed().scene)
                    val inspection = inspectCharacterInScene().getOrThrow()

                    inspection.assertHasAllSources()
                }
            )
        }

    }

    @Nested
    inner class `Should output character desire` {

        @Test
        fun `implicit character never has desire`() {
            sceneRepository.givenScene(scene.withCharacter(character.id)!!.removed().scene)
            makeStoryEvent(sceneId = scene.id).withCharacterInvolved(character)
                .storyEvent.let(storyEventRepository::givenStoryEvent)

            val inspection = inspectCharacterInScene().getOrThrow()

            inspection.desire.shouldBeBlank()
        }

        @Test
        fun `should output explicit character's desire`() {
            scene.withCharacter(character.id)!!.desireChanged("Get dat bread")
                .scene.let(sceneRepository::givenScene)

            val inspection = inspectCharacterInScene().getOrThrow()

            inspection.desire.shouldBeEqualTo("Get dat bread")
        }

    }

    @Nested
    inner class `Should output character motivation` {

        @Test
        fun `implicit character never has motivation`() {
            sceneRepository.givenScene(scene.withCharacter(character.id)!!.removed().scene)
            makeStoryEvent(sceneId = scene.id).withCharacterInvolved(character)
                .storyEvent.let(storyEventRepository::givenStoryEvent)

            val inspection = inspectCharacterInScene().getOrThrow()

            inspection.motivation.shouldBeNull()
        }

        @Test
        fun `should output explicit character's motivation`() {
            scene.withCharacter(character.id)!!.motivationChanged("Get dat bread")
                .scene.let(sceneRepository::givenScene)

            val inspection = inspectCharacterInScene().getOrThrow()

            inspection.motivation.shouldBeEqualTo("Get dat bread")
            inspection.otherMotivations.forEach { it.character.shouldBeEqualTo(character.id) }
            inspection.otherMotivations.single().sceneName.shouldBeEqualTo(scene.name.value)
        }

    }

    @Test
    fun `character has inherited motivation from previous scene`() {
        val previousScene = makeScene(projectId = scene.projectId)
            .givenCharacter(character)
            .withCharacter(character.id)!!.motivationChanged("Initial Motivation")
            .scene.also(sceneRepository::givenScene)
        sceneRepository.sceneOrders[scene.projectId] =
            SceneOrder.reInstantiate(scene.projectId, listOf(previousScene.id, scene.id))

        val inspection = inspectCharacterInScene().getOrThrow()

        inspection.otherMotivations.forEach { it.character.shouldBeEqualTo(character.id) }
        with(inspection.inheritedMotivation!!) {
            sceneId.shouldBeEqualTo(previousScene.id)
            sceneName.shouldBeEqualTo(previousScene.name.value)
            motivation.shouldBeEqualTo("Initial Motivation")
        }
    }

    private fun inspectCharacterInScene(): Result<CharacterInSceneInspection> {
        val useCase: InspectCharacterInScene =
            InspectCharacterInSceneUseCase(sceneRepository, characterRepository, storyEventRepository)
        var result: Result<CharacterInSceneInspection> = failure(Error("No response received"))
        runBlocking {
            useCase.invoke(scene.id, character.id) {
                result = it
            }
        }
        return result
    }

}