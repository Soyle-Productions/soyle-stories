package com.soyle.stories.scene.characters.tool

import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.scene.SceneLocale
import com.soyle.stories.scene.characters.SceneCharactersLocale
import com.soyle.stories.scene.characters.list.CharactersInSceneListLocale
import javafx.beans.binding.StringExpression
import javafx.beans.value.ObservableValue

interface SceneCharactersToolLocale : SceneLocale.CommonToolLocale {

    val sceneCharactersTitle: StringExpression
    val noSceneSelectedInviteMessage: ObservableMarkdownString

    val list: CharactersInSceneListLocale

}