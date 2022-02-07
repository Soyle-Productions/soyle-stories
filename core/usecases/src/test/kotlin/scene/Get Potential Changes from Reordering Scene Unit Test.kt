package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.scene.order.exceptions.sceneAlreadyAtIndex
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.common.AffectedCharacter
import com.soyle.stories.usecase.scene.common.AffectedScene
import com.soyle.stories.usecase.scene.reorderScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.reorderScene.GetPotentialChangesFromReorderingSceneUseCase
import com.soyle.stories.usecase.scene.reorderScene.PotentialChangesFromReorderingScene
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Get Potential Changes from Reordering Scene Unit Test` {

    // Prerequisites
    private val scene = makeScene()

    // output
    private var potentialChanges: PotentialChangesFromReorderingScene? = null

    private val sceneRepository = SceneRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()

    @Test
    fun `given scene doesn't exist - should return error`() {
        val error = getPotentialChanges()

        error.shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `given no other scenes exist - should return error`() {
            val error = getPotentialChanges()

            error.shouldBeEqualTo(sceneAlreadyAtIndex(scene.id, 0))
        }

        @Nested
        inner class `Given Other Scenes Exist` {

            private val sceneA = sceneRepository.givenScene(makeScene(projectId = scene.projectId))
            private val sceneB = scene
            private val sceneC = sceneRepository.givenScene(makeScene(projectId = scene.projectId))

            init {
                sceneRepository.sceneOrders[scene.projectId] = SceneOrder.reInstantiate(
                    scene.projectId,
                    listOf(sceneA, sceneB, sceneC).map { it.id }
                )
            }

            @Test
            fun `given no characters included - should output nothing`() {
                getPotentialChanges().shouldBeNull()

                potentialChanges.shouldNotBeNull().affectedScenes.shouldBeEmpty()
            }

            @Nested
            inner class `Given Character Included` {

                private val character = makeCharacter(projectId = scene.projectId)

                @Test
                fun `given character no longer exists - should output nothing`() {
                    sceneA.givenCharacter(character)                  .also(sceneRepository::givenScene)
                    sceneB.givenCharacter(character, motivation = "y").also(sceneRepository::givenScene)

                    getPotentialChanges().shouldBeNull()

                    potentialChanges.shouldNotBeNull().affectedScenes.shouldBeEmpty()
                }

                @Nested
                inner class `Given Character Still Exists` {

                    init {
                        characterRepository.givenCharacter(character)
                    }

                    @Test
                    fun `scene before starting point is affected`() {
                        sceneA.givenCharacter(character)                  .also(sceneRepository::givenScene)
                        sceneB.givenCharacter(character, motivation = "y").also(sceneRepository::givenScene)

                        getPotentialChanges(newIndex = 0).shouldBeNull()

                        potentialChanges.shouldHaveSingleScene(sceneA) {
                            it.shouldHaveSingleCharacter(character) {
                                it.currentMotivation.shouldBeEqualTo("")
                                it.potentialMotivation.shouldBeEqualTo("y")
                            }
                        }
                    }

                    @Test
                    fun `the scene itself is affected`() {
                        sceneA.givenCharacter(character, motivation = "y").also(sceneRepository::givenScene)
                        sceneB.givenCharacter(character)                  .also(sceneRepository::givenScene)

                        getPotentialChanges(newIndex = 0).shouldBeNull()

                        potentialChanges.shouldHaveSingleScene(sceneB) {
                            it.shouldHaveSingleCharacter(character) {
                                it.currentMotivation.shouldBeEqualTo("y")
                                it.potentialMotivation.shouldBeEqualTo("")
                            }
                        }
                    }

                    @Test
                    fun `scene after starting point is affected`() {
                        sceneA.givenCharacter(character, motivation = "z").also(sceneRepository::givenScene)
                        sceneB.givenCharacter(character, motivation = "y").also(sceneRepository::givenScene)
                        sceneC.givenCharacter(character)                  .also(sceneRepository::givenScene)

                        getPotentialChanges(newIndex = 0).shouldBeNull()

                        potentialChanges.shouldHaveSingleScene(sceneC) {
                            it.shouldHaveSingleCharacter(character) {
                                it.currentMotivation.shouldBeEqualTo("y")
                                it.potentialMotivation.shouldBeEqualTo("z")
                            }
                        }
                    }

                    @Test
                    fun `different motivations mean scene is affected`() {
                        sceneA.givenCharacter(character, motivation = "z").also(sceneRepository::givenScene)
                        sceneB.givenCharacter(character, motivation = "y").also(sceneRepository::givenScene)
                        sceneC.givenCharacter(character)                  .also(sceneRepository::givenScene)

                        getPotentialChanges(sceneA.id, 1).shouldBeNull()

                        potentialChanges.shouldHaveSingleScene(sceneC) {
                            it.shouldHaveSingleCharacter(character) {
                                it.currentMotivation.shouldBeEqualTo("y")
                                it.potentialMotivation.shouldBeEqualTo("z")
                            }
                        }
                    }

                    @Test
                    fun `given multiple scenes and characters affected`() {
                        val characters = listOf(
                            character,
                            makeCharacter(projectId = scene.projectId),
                            makeCharacter(projectId = scene.projectId),
                            makeCharacter(projectId = scene.projectId)
                        ).onEach(characterRepository::givenCharacter)

                        sceneA
                            .givenCharacter(characters[0], "z")
                            .givenCharacter(characters[3])
                            .also(sceneRepository::givenScene)
                        sceneB
                            .givenCharacter(characters[1])
                            .givenCharacter(characters[2], "x")
                            .also(sceneRepository::givenScene)
                        sceneC
                            .givenCharacter(characters[0])
                            .givenCharacter(characters[2], "w")
                            .givenCharacter(characters[3], "v")
                            .also(sceneRepository::givenScene)
                        val sceneD = makeScene(projectId = scene.projectId)
                            .givenCharacter(characters[1], "y")
                            .givenCharacter(characters[3])
                            .also(sceneRepository::givenScene)
                        val sceneE = makeScene(projectId = scene.projectId)
                            .givenCharacter(characters[0])
                            .givenCharacter(characters[1])
                            .givenCharacter(characters[2])
                            .givenCharacter(characters[3])
                            .also(sceneRepository::givenScene)

                        getPotentialChanges(sceneC.id, 0)

                        with(potentialChanges.shouldNotBeNull().affectedScenes) {
                            shouldHaveSize(3)
                            shouldHaveSceneBackedBy(sceneA) {
                                it.shouldHaveSingleCharacter(characters[3]) {
                                    it.currentMotivation.shouldBeEqualTo("")
                                    it.potentialMotivation.shouldBeEqualTo("v")
                                }
                            }
                            shouldHaveSceneBackedBy(sceneC) {
                                it.shouldHaveSingleCharacter(characters[0]) {
                                    it.currentMotivation.shouldBeEqualTo("z")
                                    it.potentialMotivation.shouldBeEqualTo("")
                                }
                            }
                            shouldHaveSceneBackedBy(sceneE) {
                                it.shouldHaveSingleCharacter(characters[2]) {
                                    it.currentMotivation.shouldBeEqualTo("w")
                                    it.potentialMotivation.shouldBeEqualTo("x")
                                }
                            }
                        }
                    }

                }

            }
        }
    }

    private fun getPotentialChanges(sceneId: Scene.Id = scene.id, newIndex: Int = 0): Throwable? {
        val useCase: GetPotentialChangesFromReorderingScene =
            GetPotentialChangesFromReorderingSceneUseCase(sceneRepository, characterRepository)
        return runBlocking {
            useCase(sceneId, newIndex) {
                potentialChanges = it
            }
        }
    }

    private fun Scene.givenCharacter(character: Character, motivation: String? = null): Scene = withCharacterIncluded(character)
        .scene.withCharacter(character.id)!!.motivationChanged(motivation).scene

    private fun PotentialChangesFromReorderingScene?.shouldHaveSingleScene(backingScene: Scene, sceneExpectations: (AffectedScene) -> Unit) {
        shouldNotBeNull().affectedScenes.shouldHaveSingleItem().run {
            shouldBeBackedBy(backingScene)
            sceneExpectations(this)
        }
    }

    private fun List<AffectedScene>.shouldHaveSceneBackedBy(backingScene: Scene, sceneExpectations: (AffectedScene) -> Unit) {
        val affectedScene = single { it.sceneId == backingScene.id }
        affectedScene.shouldBeBackedBy(backingScene)
        sceneExpectations(affectedScene)
    }

    private fun AffectedScene.shouldHaveSingleCharacter(backingCharacter: Character, characterExpectations: (AffectedCharacter) -> Unit) {
        characters.shouldHaveSingleItem().run {
            characterId.shouldBeEqualTo(backingCharacter.id.uuid)
            characterExpectations(this)
            characterName.shouldBeEqualTo(backingCharacter.displayName.value)
        }
    }

    private fun AffectedScene.shouldBeBackedBy(backingScene: Scene) {
        sceneId.shouldBeEqualTo(backingScene.id)
        sceneName.shouldBeEqualTo(backingScene.name.value)
    }

    /*

//    All scenes ordered after the requested index and after the original index can potentially be affected by a scene
//    being reordered.  The scene itself can be affected if any of its characters inherit motivations from previous scenes.
//    This use case, for each character in the scene being moved, lists the scenes that would have a character motivation
//    modified by the change.






    @Test
    fun `common case`() {
        givenScenesToCharacters("""
               A, B, C, D, E
            1: z, _, -, _, -
            2: _, -, _, y, -
            3: _, x, w, _, -
            4: -, _, v, -, -
        """)
        potentialChangesForMoving("C", to=0)
        expectResult(
            "A" to listOf(
                "4" to "- -> v"
            ),
            "C" to listOf(
                "1" to "z -> -"
            ),
            "E" to listOf(
                "3" to "w -> x"
            )
        )
    }

    private val sceneRepository = SceneRepositoryDouble()
    private val sceneNameMap = mutableMapOf<String, Scene.Id>()
    private val characterIdMap = mutableMapOf<String, Character.Id>()
    private val characterMap = mutableMapOf<Character.Id, Character>()

    private fun givenScenes(vararg names: String)
    {
        names.forEach {
            val scene = makeScene(projectId = projectId, name = NonBlankString.create(it)!!)
            sceneRepository.givenScene(scene)
            sceneNameMap[it] = scene.id
        }
    }

    private fun givenScenesToCharacters(init: String) {
        val lines = init.trimIndent().split("\n")
        val sceneLine = lines.firstOrNull() ?: return
        val scenes = sceneLine.split(",").map(String::trim)
        givenScenes(*scenes.toTypedArray())
        lines.drop(1).forEach { line ->
            val (characterIdentifier, motiveLine) = line.split(":")
            val character = characterFor(characterIdentifier)
            val motives = motiveLine.split(",").map(String::trim)
            motives.forEachIndexed { index, motive ->
                val scene = sceneRepository.scenes.getValue(idOf(scenes[index]))
                val newScene = when (motive) {
                    "_" -> return@forEachIndexed
                    "-" -> scene.withCharacterIncluded(character).scene
                    else -> scene.withCharacterIncluded(character).scene.withMotivationForCharacter(character.id, motive)
                }
                sceneRepository.scenes[scene.id] = newScene
            }
        }
    }

    private fun idOf(scene: String): Scene.Id = sceneNameMap.getOrPut(scene) { Scene.Id() }
    private fun characterFor(character: String): Character = characterIdMap.getOrPut(character) { Character.Id() }
        .let { characterMap.getOrPut(it) { makeCharacter(it, projectId, characterName()) } }

    private fun potentialChangesForMoving(scene: String, to: Int = 0)
    {
        val sceneId = idOf(scene)
        val useCase: GetPotentialChangesFromReorderingScene = GetPotentialChangesFromReorderingSceneUseCase(sceneRepository)
        val output = object : GetPotentialChangesFromReorderingScene.OutputPort {
            override fun receivePotentialChangesFromReorderingScene(response: PotentialChangesFromReorderingScene) {
                result = response
            }
        }
        runBlocking {
            try {
                useCase.invoke(sceneId.uuid, to, output)
            } catch (t: Throwable) {
                result = t
            }
        }
    }

    private fun expectEmptyResult() = expectResult()

    private fun expectResult(vararg scenes: Pair<String, List<Pair<String, String>>>)
    {
        val actual = result as PotentialChangesFromReorderingScene
        val includedSet = mutableSetOf<String>()
        val missingSet = mutableSetOf<String>()
        val sceneUuidToKey = sceneNameMap.entries.associate { it.value.uuid to it.key }
        scenes.forEach { (sceneIdentifier, expectedCharacters) ->
            val sceneId = idOf(sceneIdentifier).uuid
            includedSet.add(sceneIdentifier)
            val scene = actual.affectedScenes.find { it.sceneId == sceneId}
                ?: return@forEach Unit.also { missingSet.add(sceneIdentifier) }
            expectedCharacters.forEach { (characterIdentifier, expectedChange) ->
                val characterId = characterIdMap.getValue(characterIdentifier).uuid
                val character = scene.characters.find { it.characterId == characterId }
                    ?: fail("Missing character $characterIdentifier\n" + incorrectOutput(actual, *scenes))
                val (expectedCurrentMotive, expectedNewMotive) = expectedChange.split(" -> ")
                assertEquals(expectedCurrentMotive.toActual(), character.currentMotivation) {
                    "Current Character Motivation for character $characterIdentifier in scene $sceneIdentifier is incorrect.\n${incorrectOutput(actual, *scenes)}"
                }
                assertEquals(expectedNewMotive.toActual(), character.potentialMotivation) {
                    "Potential Character Motivation for character $characterIdentifier in scene $sceneIdentifier is incorrect.\n${incorrectOutput(actual, *scenes)}"
                }
            }
        }
        val extraScenes = (actual.affectedScenes.map { sceneUuidToKey.getValue(it.sceneId) }.toSet() - includedSet)
        if (extraScenes.isNotEmpty() || missingSet.isNotEmpty()) {
            fail<Nothing>("""
                Invalid Output.
                Extraneous Scenes: $extraScenes
                Missing Scenes: $missingSet
            """.trimIndent() + incorrectOutput(actual, *scenes))
        }
    }

    private fun String.toActual(): String = when (this) {
        "-", "_" -> ""
        else -> this
    }

    private fun incorrectOutput(
        actual: PotentialChangesFromReorderingScene,
        vararg scenes: Pair<String, List<Pair<String, String>>>
    ): String {
        return StringBuilder()
            .append("Expected Output: ${format(scenes.toList())}\n")
            .append("Output Received: ${actual.format()}")
            .toString()
    }

    private fun format(scenes: List<Pair<String, List<Pair<String, String>>>>): String
    {
        val s = StringBuilder()
        scenes.forEach {
            s.append("\n${it.first}: {\n")
            it.second.forEach {
                s.append("\t${it.first}: ${it.second}\n")
            }
            s.append("}")
        }
        return s.toString()
    }

    private fun PotentialChangesFromReorderingScene.format(): String
    {
        val sceneUuidToKey = sceneNameMap.entries.associate { it.value.uuid to it.key }
        val characterUuidToKey = characterIdMap.entries.associate { it.value.uuid to it.key }
        val s = StringBuilder()
        affectedScenes.forEach {
            s.append("\n${sceneUuidToKey.getValue(it.sceneId)}: {\n")
            it.characters.forEach {
                s.append("\t${characterUuidToKey.getValue(it.characterId)}: ${it.currentMotivation} -> ${it.potentialMotivation}\n")
            }
            s.append("}")
        }
        return s.toString()
    }

     */
}
