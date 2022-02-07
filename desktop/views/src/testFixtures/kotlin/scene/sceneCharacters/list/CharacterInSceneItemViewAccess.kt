package com.soyle.stories.desktop.view.scene.sceneCharacters.list

import com.soyle.stories.common.ViewOf
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemViewModel
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Labeled
import javafx.scene.control.Tooltip
import tornadofx.CssRule

class CharacterInSceneItemViewAccess (private val view: ViewOf<CharacterInSceneItemViewModel>) : NodeAccess<Node>(view as Node)  {

    val editButton: Button
        get() = from(node).lookup("Edit").queryButton()!!

    val name: Labeled by mandatoryChild(TextStyles.sectionTitle)

    val role: Labeled by mandatoryChild(TextStyles.caption)

    val warning: Tooltip?
        get() = name.tooltip

}


fun ViewOf<CharacterInSceneItemViewModel>.access() = CharacterInSceneItemViewAccess(this)