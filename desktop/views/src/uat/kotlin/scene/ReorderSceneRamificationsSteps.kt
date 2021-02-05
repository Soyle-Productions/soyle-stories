package com.soyle.stories.scene

import com.soyle.stories.character.CharacterDriver
import com.soyle.stories.di.get
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.reorderSceneRamifications.ReorderSceneRamifications
import com.soyle.stories.scene.reorderSceneRamifications.ReorderSceneRamificationsScope
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import io.cucumber.java8.En
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Labeled
import javafx.scene.control.TextField
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest

class ReorderSceneRamificationsSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {
        fun getToolScopeForSceneIdentifier(
            double: SoyleStoriesTestDouble,
            sceneIdentifier: String
        ): ReorderSceneRamificationsScope? {
            val scene = ScenesDriver.getSceneIdByIdentifier(double, sceneIdentifier)!!
            val projectScope = ProjectSteps.getProjectScope(double)!!
            val scope = projectScope.toolScopes
                .asSequence()
                .filterIsInstance<ReorderSceneRamificationsScope>()
                .find { it.sceneId == scene.uuid.toString() }
            return scope
        }

        private fun getTool(
            double: SoyleStoriesTestDouble,
            toolSceneIdentifier: String
        ): ReorderSceneRamifications? {
            val scope = getToolScopeForSceneIdentifier(double, toolSceneIdentifier) ?: return null
            return findComponentsInScope<ReorderSceneRamifications>(scope).firstOrNull()
        }

        private fun getListedSceneNodes(
            double: SoyleStoriesTestDouble,
            toolSceneIdentifier: String
        ): List<Parent> {
            val root = getTool(double, toolSceneIdentifier)?.root
            return from(root).lookup(".scene-item").queryAll<Parent>().toList()
        }

        private fun getListedSceneNode(
            double: SoyleStoriesTestDouble,
            toolSceneIdentifier: String,
            listedSceneIdentifier: String
        ): Parent? {
            val listedSceneId = ScenesDriver.getSceneIdByIdentifier(double, listedSceneIdentifier)!!
            return getListedSceneNodes(double, toolSceneIdentifier).find { it.id == listedSceneId.uuid.toString() }
        }

        private fun getListedCharacterNodesInListedScene(
            double: SoyleStoriesTestDouble,
            toolSceneIdentifier: String,
            listedSceneIdentifier: String
        ): List<Node>
        {
            val sceneNode = getListedSceneNode(double, toolSceneIdentifier, listedSceneIdentifier)
            return from(sceneNode).lookup(".character-item").queryAll<Node>().toList()
        }

        private fun getListedCharacterNode(
            double: SoyleStoriesTestDouble,
            toolSceneIdentifier: String,
            listedSceneIdentifier: String,
            listedCharacterIdentifier: String
        ): Node? {
            val listedCharacterId = CharacterDriver.getCharacterIdByIdentifier(double, listedCharacterIdentifier)!!
            return getListedCharacterNodesInListedScene(double, toolSceneIdentifier, listedSceneIdentifier)
                .find { it.id == listedCharacterId.uuid.toString() }
        }

        private fun getListedCharacterCurrentMotiveField(
            double: SoyleStoriesTestDouble,
            toolSceneIdentifier: String,
            listedSceneIdentifier: String,
            listedCharacterIdentifier: String
        ): TextField {
            val characterNode = getListedCharacterNode(double, toolSceneIdentifier, listedSceneIdentifier, listedCharacterIdentifier)
            return from(characterNode).lookup(".current").queryAll<TextField>().first()
        }

        private fun getListedCharacterChangedMotiveField(
            double: SoyleStoriesTestDouble,
            toolSceneIdentifier: String,
            listedSceneIdentifier: String,
            listedCharacterIdentifier: String
        ): TextField {
            val characterNode = getListedCharacterNode(double, toolSceneIdentifier, listedSceneIdentifier, listedCharacterIdentifier)
            return from(characterNode).lookup(".changed").queryAll<TextField>().first()
        }
    }

    init {
        with(en) {


            Given("the {string} Reorder Scene Ramifications Tool has been opened to move to index {int}") {
                    sceneIdentifier: String, index: Int ->
                val scene = ScenesDriver.getSceneByIdentifier(double, sceneIdentifier)!!
                ReorderSceneDialogSteps.setReorderRequest(double, scene to index)
                val projectScope = ProjectSteps.getProjectScope(double)!!
                projectScope.get<OpenToolController>().openReorderSceneRamificationsTool(
                    scene.id.uuid.toString(), index
                )
            }

            When("the {string} Reorder Scene Ramifications Tool is opened to move to index {int}") {
                    sceneIdentifier: String, index: Int ->
                val scene = ScenesDriver.getSceneIdByIdentifier(double, sceneIdentifier)!!
                val projectScope = ProjectSteps.getProjectScope(double)!!
                projectScope.get<OpenToolController>().openReorderSceneRamificationsTool(
                    scene.uuid.toString(), index
                )
            }
            When("the {string} Reorder Scene Ramifications Tool {string} button is selected") {
                    sceneIdentifier: String, buttonLabel: String ->
                val button = from(getTool(double, sceneIdentifier)!!.root).lookup(".button").queryAll<Button>().find {
                    it.text == buttonLabel
                }!!
                DeleteSceneRamificationsSteps.interact {
                    button.fire()
                }
            }

            Then("the Reorder Scene Ramifications Tool should be open") {
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val tools = projectScope.toolScopes
                    .asSequence()
                    .filterIsInstance<ReorderSceneRamificationsScope>()
                    .flatMap { findComponentsInScope<ReorderSceneRamifications>(it).asSequence() }.toList()
                assertTrue(tools.isNotEmpty())
            }
            Then("the {string} Reorder Scene Ramifications Tool should display an ok message") {
                    sceneIdentifier: String ->
                val root = getTool(double, sceneIdentifier)?.root
                val emptyDisplay = from(root).lookup(".empty-display").queryAll<Node>().firstOrNull()
                assertTrue(emptyDisplay?.visibleProperty()?.get() == true)
            }
            Then("the scene {string} should be listed in the {string} Reorder Scene Ramifications Tool") {
                listedSceneIdentifier: String, toolSceneIdentifier: String ->
                assertNotNull(getListedSceneNode(double, toolSceneIdentifier, listedSceneIdentifier))
            }
            Then("the scene {string} should not be listed in the {string} Reorder Scene Ramifications Tool") {
                    listedSceneIdentifier: String, toolSceneIdentifier: String ->
                assertNull(getListedSceneNode(double, toolSceneIdentifier, listedSceneIdentifier))
            }
            Then("the character {string} should be listed for {string} in the {string} Reorder Scene Ramifications Tool") {
                    listedCharacterIdentifier: String, listedSceneIdentifier: String, toolSceneIdentifier: String ->

                getListedCharacterNode(double, toolSceneIdentifier, listedSceneIdentifier, listedCharacterIdentifier)
                    .let(::assertNotNull)
            }
            Then("the character {string} should not be listed in the {string} Reorder Scene Ramifications Tool") {
                    listedCharacterIdentifier: String, toolSceneIdentifier: String ->

                val listedCharacterId = CharacterDriver.getCharacterIdByIdentifier(double, listedCharacterIdentifier)!!

                getListedSceneNodes(double, toolSceneIdentifier).forEach {
                    from(it).lookup(".character-item").queryAll<Node>().find {
                        it.id == listedCharacterId.uuid.toString()
                    }.let(::assertNull)
                }
            }
            Then("the {string} Reorder Scene Ramifications Current Motivation field for {string} in {string} should show {string}") {
                    toolSceneIdentifier: String, listedCharacterIdentifier: String, listedSceneIdentifier: String, expectedValue: String ->

                val field = getListedCharacterCurrentMotiveField(double, toolSceneIdentifier, listedSceneIdentifier, listedCharacterIdentifier)
                assertEquals(expectedValue, field.text)
            }
            Then("the {string} Reorder Scene Ramifications Current Motivation field for {string} in {string} should be empty") {
                    toolSceneIdentifier: String, listedCharacterIdentifier: String, listedSceneIdentifier: String ->

                val field = getListedCharacterCurrentMotiveField(double, toolSceneIdentifier, listedSceneIdentifier, listedCharacterIdentifier)
                assertTrue(field.text!!.isEmpty())
            }
            Then("the {string} Reorder Scene Ramifications Changed Motivation field for {string} in {string} should show {string}") {
                    toolSceneIdentifier: String, listedCharacterIdentifier: String, listedSceneIdentifier: String, expectedValue: String ->

                val field = getListedCharacterChangedMotiveField(double, toolSceneIdentifier, listedSceneIdentifier, listedCharacterIdentifier)
                assertEquals(expectedValue, field.text)
            }
            Then("the {string} Reorder Scene Ramifications Changed Motivation field for {string} in {string} should be empty") {
                    toolSceneIdentifier: String, listedCharacterIdentifier: String, listedSceneIdentifier: String ->

                val field = getListedCharacterChangedMotiveField(double, toolSceneIdentifier, listedSceneIdentifier, listedCharacterIdentifier)
                assertTrue(field.text!!.isEmpty())
            }
            Then("the {string} Reorder Scene Ramifications Tool should be closed") { sceneIdentifier: String ->
                assertNull(getTool(double, sceneIdentifier))
            }
        }
    }

}