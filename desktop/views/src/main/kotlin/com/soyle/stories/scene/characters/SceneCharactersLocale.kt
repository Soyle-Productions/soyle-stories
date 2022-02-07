package com.soyle.stories.scene.characters

import com.soyle.stories.scene.characters.list.CharactersInSceneListLocale
import com.soyle.stories.scene.characters.remove.ConfirmRemoveCharacterFromScenePromptLocale
import com.soyle.stories.scene.characters.tool.SceneCharactersToolLocale

interface SceneCharactersLocale {
    val list: CharactersInSceneListLocale
    val tool: SceneCharactersToolLocale
    val remove: ConfirmRemoveCharacterFromScenePromptLocale
}