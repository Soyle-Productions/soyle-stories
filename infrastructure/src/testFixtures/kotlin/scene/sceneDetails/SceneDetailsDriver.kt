package com.soyle.stories.desktop.view.scene.sceneDetails

import com.soyle.stories.scene.sceneDetails.SceneDetails
import javafx.scene.Node
import javafx.scene.control.MenuButton
import javafx.scene.control.TextInputControl
import javafx.scene.layout.VBox
import org.testfx.api.FxRobot

class SceneDetailsDriver(private val sceneDetails: SceneDetails) : FxRobot() {

    fun getIncludeCharacterMenu(): MenuButton = from(sceneDetails.root).lookup(".add-character").query()
    fun getIncludedCharacter(characterId: String): IncludedCharacterDriver {
        val node: VBox = from(sceneDetails.root).lookup(".included-character").queryAll<VBox>().find { it.id == characterId }!!
        return IncludedCharacterDriver(node)
    }

    inner class IncludedCharacterDriver(private val node: Node) {
        private fun getMotivationField(): Node = from(node).lookup(".motivation").query()
        fun getMotivationFieldInput(): TextInputControl = from(getMotivationField()).lookup(".text-field").queryTextInputControl()
    }


}