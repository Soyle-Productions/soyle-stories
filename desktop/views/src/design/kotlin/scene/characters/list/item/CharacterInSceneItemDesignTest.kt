package com.soyle.stories.desktop.view.scene.characters.list.item

import com.soyle.stories.desktop.locale.LocaleHolder
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItem
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemLocale
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemViewModel
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneSourceItem
import javafx.beans.binding.StringExpression
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.layout.Pane
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import tornadofx.objectProperty
import tornadofx.stringProperty

class CharacterInSceneItemDesignTest : DesignTest() {

    private val itemState = objectProperty(
        CharacterInSceneItem(
            Character.Id(),
            Scene.Id(),
            Project.Id(),
            "Bob",
            isExplicit = true,
            roleInScene = null,
            sources = setOf(
                CharacterInSceneSourceItem(StoryEvent.Id(), "A thing that happens")
            )
        )
    )
    private val viewModel = CharacterInSceneItemViewModel(itemState, locale = LocaleHolder().scenes.characters.list.item)
    override val node: Node
        get() = CharacterInSceneItem(viewModel)

    private fun testState(
        roleInScene: RoleInScene?,
        inProject: Boolean,
        hasSources: Boolean,
        isExplicit: Boolean
    ) = dynamicTest("Role: $roleInScene, inProject: $inProject, hasSources: $hasSources, isExplicit: $isExplicit") {
        itemState.set(itemState.get().copy(
            roleInScene = roleInScene,
            project = Project.Id().takeIf { inProject },
            isExplicit = isExplicit,
            sources = itemState.get().sources.takeIf { hasSources }.orEmpty()
        ))
        verifyDesign()
    }

    @TestFactory
    fun DesignStates() = listOf(
        testState(roleInScene = null, inProject = true, hasSources = true, isExplicit = true),
        testState(roleInScene = null, inProject = true, hasSources = true, isExplicit = false),
        testState(roleInScene = null, inProject = true, hasSources = false, isExplicit = true),
        testState(roleInScene = null, inProject = false, hasSources = true, isExplicit = true),
        testState(roleInScene = null, inProject = false, hasSources = true, isExplicit = false),
        testState(roleInScene = null, inProject = false, hasSources = false, isExplicit = true),

        testState(roleInScene = RoleInScene.IncitingCharacter, inProject = true, hasSources = true, isExplicit = true),
        testState(roleInScene = RoleInScene.IncitingCharacter, inProject = true, hasSources = true, isExplicit = false),
        testState(roleInScene = RoleInScene.IncitingCharacter, inProject = true, hasSources = false, isExplicit = true),
        testState(roleInScene = RoleInScene.IncitingCharacter, inProject = false, hasSources = true, isExplicit = true),
        testState(roleInScene = RoleInScene.IncitingCharacter, inProject = false, hasSources = true, isExplicit = false),
        testState(roleInScene = RoleInScene.IncitingCharacter, inProject = false, hasSources = false, isExplicit = true),

        testState(roleInScene = RoleInScene.OpponentCharacter, inProject = true, hasSources = true, isExplicit = true),
        testState(roleInScene = RoleInScene.OpponentCharacter, inProject = true, hasSources = true, isExplicit = false),
        testState(roleInScene = RoleInScene.OpponentCharacter, inProject = true, hasSources = false, isExplicit = true),
        testState(roleInScene = RoleInScene.OpponentCharacter, inProject = false, hasSources = true, isExplicit = true),
        testState(roleInScene = RoleInScene.OpponentCharacter, inProject = false, hasSources = true, isExplicit = false),
        testState(roleInScene = RoleInScene.OpponentCharacter, inProject = false, hasSources = false, isExplicit = true),
    )


}