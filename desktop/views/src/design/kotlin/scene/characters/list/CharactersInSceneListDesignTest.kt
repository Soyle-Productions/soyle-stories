package com.soyle.stories.desktop.view.scene.characters.list

import com.soyle.stories.desktop.locale.LocaleHolder
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.characters.list.CharactersInScene
import com.soyle.stories.scene.characters.list.CharactersInSceneViewModel
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneSourceItem
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import io.mockk.mockk
import javafx.scene.Node
import org.junit.jupiter.api.Test
import tornadofx.objectProperty

class CharactersInSceneListDesignTest : DesignTest() {

    private val locale = LocaleHolder().scenes.characters.list

    private val itemState = objectProperty<CharactersInScene>(
        CharactersInScene(
            Scene.Id(),
            "Big Battle",
            emptyList()
        )
    )
    private val viewModel = CharactersInSceneViewModel(
        itemState
    )

    override val node: Node
        get() = locale.CharactersInScene(viewModel)

    @Test
    fun empty() {
        verifyDesign()
    }

    @Test
    fun `has items`() {
        itemState.set(itemState.get().copy(items = listOf(
            "Bob",
            "Frank",
            "Alice",
            "Hank",
            "George"
        ).map {
            CharacterInSceneItem(
                Character.Id(),
                Scene.Id(),
                Project.Id(),
                it,
                isExplicit = true,
                roleInScene = null,
                sources = setOf(
                    CharacterInSceneSourceItem(StoryEvent.Id(), "A thing that happens")
                )
            )
        }))
        verifyDesign()
    }

}