package com.soyle.stories.desktop.view.scene.characters.inspect

import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspection
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionViewModel
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItem
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemViewModel
import javafx.scene.Node
import org.junit.jupiter.api.Test
import tornadofx.addClass
import tornadofx.button
import tornadofx.objectProperty

class CharacterInSceneInspectionDesignTest : DesignTest() {

    private val itemViewModel = CharacterInSceneItemViewModel(
        objectProperty(CharacterInSceneItem(Character.Id(), Scene.Id(), Project.Id(), "Bob", true, null, emptySet()))
    )
    private val inspectionState = objectProperty<CharacterInSceneInspection?>(CharacterInSceneInspection(itemViewModel.item, "", listOf(), mapOf()))
    private val viewModel = CharacterInSceneInspectionViewModel(itemViewModel, inspectionState)

    override val node: Node
        get() = CharacterInSceneInspection(viewModel, CharacterInSceneItem(itemViewModel) {
            button("DONE") {
                addClass(primary, filled)
            }
        })

    @Test
    fun default() {
        verifyDesign()
    }

}