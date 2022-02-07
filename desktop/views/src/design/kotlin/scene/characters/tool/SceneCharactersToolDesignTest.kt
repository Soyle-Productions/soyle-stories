package com.soyle.stories.desktop.view.scene.characters.tool

import com.soyle.stories.common.doNothing
import com.soyle.stories.desktop.locale.LocaleHolder
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.characters.list.CharactersInSceneViewModel
import com.soyle.stories.scene.characters.tool.SceneCharactersTool
import com.soyle.stories.scene.characters.tool.SceneCharactersToolViewModel
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneSourceItem
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import javafx.scene.Node
import org.junit.jupiter.api.Test
import tornadofx.booleanProperty
import tornadofx.objectProperty
import tornadofx.stringProperty

class SceneCharactersToolDesignTest : DesignTest() {

    private val selectedScene =
        objectProperty<SceneCharactersToolViewModel.SceneSelection>(SceneCharactersToolViewModel.SceneSelection.None)
    private val characterFocus = objectProperty<SceneCharactersToolViewModel.CharacterFocus?>()
    private val viewModel = SceneCharactersToolViewModel(
        selectedScene,
        characterFocus
    )
    override val node: Node
        get() = SceneCharactersTool(viewModel, LocaleHolder().scenes.characters.tool)

    @Test
    fun `no scene selected`() {
        verifyDesign()
    }

    @Test
    fun `loading selected scene`() {
        selectedScene.set(
            SceneCharactersToolViewModel.SceneSelection.Loading(
                Scene.Id(),
                stringProperty("Big Battle")
            )
        )
        verifyDesign()
    }

    @Test
    fun `selected scene loaded`() {
        selectedScene.set(
            SceneCharactersToolViewModel.SceneSelection.Loaded(
                CharactersInSceneViewModel(
                    objectProperty(
                        CharactersInScene(Scene.Id(), "Big Battle", items = listOf(
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
                        })
                    ),
                    onEditCharacter = {
                        characterFocus.set(
                            viewModel.CharacterFocus(
                                objectProperty(it.item),
                                objectProperty(CharacterInSceneInspection(it.item, "", listOf(), mapOf())),
                                booleanProperty(false),
                                onNavigateToPreviousScene = ::doNothing
                            )
                        )
                    }
                )
            )
        )
        verifyDesign()
    }

}