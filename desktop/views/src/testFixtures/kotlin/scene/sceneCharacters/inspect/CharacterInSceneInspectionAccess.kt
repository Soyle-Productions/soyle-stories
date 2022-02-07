package com.soyle.stories.desktop.view.scene.sceneCharacters.inspect

import com.soyle.stories.common.ViewOf
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionStyles
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionViewModel
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.Pane
import org.controlsfx.control.PopOver
import tornadofx.CssRule
import tornadofx.Stylesheet

class CharacterInSceneInspectionAccess (view: ViewOf<CharacterInSceneInspectionViewModel>) : NodeAccess<Node>(view as Node) {

    private val roleField: Pane by mandatoryChild(CharacterInSceneInspectionStyles.roleInSceneSelection)
    val incitingCharacterToggle: ToggleButton by roleField.mandatoryChild(CharacterInSceneInspectionStyles.incitingCharacter)
    val opponentCharacterToggle: ToggleButton by roleField.mandatoryChild(CharacterInSceneInspectionStyles.opponentCharacter)

    private val desireField: Pane by mandatoryChild(CssRule.c("desire"))
    val desireInput: TextInputControl by desireField.mandatoryChild(CharacterInSceneInspectionStyles.desire)

    private val motivationField: Pane by mandatoryChild(CssRule.c("motivation"))
    val motivationInput: TextInputControl by motivationField.mandatoryChild(CharacterInSceneInspectionStyles.motivation)

    private val lastSetSource: ButtonBase? by motivationField.temporaryChild(CharacterInSceneInspectionStyles.lastSetSource)
    val inheritedMotivation: Labeled?
        get() = (lastSetSource?.properties?.get("popOver") as? PopOver)?.contentNode?.let {
            it.findChild(CharacterInSceneInspectionStyles.previousMotivation)
        }

}

fun ViewOf<CharacterInSceneInspectionViewModel>.access() = CharacterInSceneInspectionAccess(this)
fun ViewOf<CharacterInSceneInspectionViewModel>.drive(op: CharacterInSceneInspectionAccess.() -> Unit) {
    access().apply { interact { op() } }
}