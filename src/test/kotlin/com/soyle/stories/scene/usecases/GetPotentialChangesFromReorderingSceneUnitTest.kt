package com.soyle.stories.scene.usecases

import arrow.core.toT
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.sceneDoesNotExist
import com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingSceneUseCase
import com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene.PotentialChangesFromReorderingScene
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.*
import kotlin.properties.Delegates

class GetPotentialChangesFromReorderingSceneUnitTest {
    /*

    All scenes ordered after the requested index and after the original index can potentially be affected by a scene
    being reordered.  The scene itself can be affected if any of its characters inherit motivations from previous scenes.
    This use case, for each character in the scene being moved, lists the scenes that would have a character motivation
    modified by the change.

     */

    private val projectId = Project.Id()

    private var result: Any? = null

    @Nested
    inner class `Scene Does Not Exist` {

        @AfterEach
        fun `should throw error`() {
            sceneDoesNotExist(idOf("A").uuid).invoke(result)
        }

        @Test
        fun `no scenes exist`() {
            potentialChangesForMoving("A", to=0)
        }

        @Test
        fun `scene not in list`() {
            givenScenes("B", "C", "D", "E")
            potentialChangesForMoving("A", to=0)
        }

    }

    @Nested
    inner class `Scene Causes no Effects` {

        @AfterEach
        fun `no scenes can be affected`() {
            expectEmptyResult()
        }

        @Test
        fun `scene has no characters`() {
            givenScenesToCharacters( """
                   A, B, C, D, E
                1: _, _, _, _, _
                2: _, _, _, _, _
            """)
            potentialChangesForMoving("C", to=0)
        }

        @Test
        fun `move to same index`() {
            givenScenesToCharacters( """
                   A, B, C, D, E
                1: _, _, z, -, _
                2: _, -, y, _, _
            """)
            potentialChangesForMoving("C", to=2)
        }

        @Test
        fun `move to (next) index`() {
            givenScenesToCharacters( """
                   A, B, C, D, E
                1: _, _, z, -, _
                2: _, -, y, _, _
            """)
            potentialChangesForMoving("C", to=3)
        }

        @Test
        fun `no other scenes include characters`() {
            givenScenesToCharacters( """
                   A, B, C, D, E
                1: _, _, z, _, _
                2: _, _, y, _, _
            """)
            potentialChangesForMoving("C", to=0)
        }

        @Test
        fun `move back and only other scenes with characters are later`() {
            givenScenesToCharacters( """
                   A, B, C, D, E
                1: _, _, z, -, -
                2: _, _, y, -, -
            """)
            potentialChangesForMoving("C", to=0)
        }
    }

    @Test
    fun `scene after starting point is affected`() {
        givenScenesToCharacters("""
               A, B, C
            1: z, y, -
        """)
        potentialChangesForMoving("B", to=0)
        expectResult(
            "C" to listOf(
                "1" to "y -> z"
            )
        )
    }

    @Test
    fun `scene before starting point is affected`() {
        givenScenesToCharacters("""
               A, B, C
            1: -, y, _
        """)
        potentialChangesForMoving("B", to=0)
        expectResult(
            "A" to listOf(
                "1" to "- -> y"
            )
        )
    }

    @Test
    fun `the scene itself is affected`() {
        givenScenesToCharacters("""
               A, B, C
            1: y, -, _
        """)
        potentialChangesForMoving("B", to=0)
        expectResult(
            "B" to listOf(
                "1" to "y -> -"
            )
        )
    }

    @Test
    fun `different motivations mean scene is affected`() {
        givenScenesToCharacters("""
               A, B, C
            1: z, y, -
        """)
        potentialChangesForMoving("A", to=2)
        expectResult(
            "C" to listOf(
                "1" to "y -> z"
            )
        )
    }

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
        sceneRepository.sceneOrder[projectId] = names.map {
            val scene = Scene(projectId, NonBlankString.create(it)!!, StoryEvent.Id())
            sceneRepository.scenes[scene.id] = scene
            sceneNameMap[it] = scene.id
            scene.id
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
                    "-" -> scene.withCharacterIncluded(character)
                    else -> scene.withCharacterIncluded(character).withMotivationForCharacter(character.id, motive)
                }
                sceneRepository.scenes[scene.id] = newScene
            }
        }
    }

    private fun idOf(scene: String): Scene.Id = sceneNameMap.getOrPut(scene) { Scene.Id() }
    private fun characterFor(character: String): Character = characterIdMap.getOrPut(character) { Character.Id() }
        .let { characterMap.getOrPut(it) { makeCharacter(it, projectId, "") } }

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
}
