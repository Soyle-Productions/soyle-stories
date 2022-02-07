package com.soyle.stories.scene.characters.list

import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.scene.characters.tool.SceneCharactersToolLocale
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemLocale
import javafx.beans.binding.StringExpression
import javafx.beans.value.ObservableValue

interface CharactersInSceneListLocale {

    val toolTitle: ObservableValue<String>

    val noCharactersInScene_inviteMessage: ObservableMarkdownString

    val addCharacter: StringExpression
    val options: StringExpression

    val item: CharacterInSceneItemLocale

}