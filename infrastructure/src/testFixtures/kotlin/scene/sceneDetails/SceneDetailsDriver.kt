package com.soyle.stories.desktop.view.scene.sceneDetails

import com.soyle.stories.common.components.menuChipGroup.MenuChipGroup
import com.soyle.stories.common.components.menuChipGroup.MenuChipGroupStyles.Companion.menuChipGroup
import com.soyle.stories.scene.sceneDetails.SceneDetails
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.VBox
import org.testfx.api.FxRobot

class SceneDetailsDriver(private val sceneDetails: SceneDetails) : FxRobot() {

    fun getIncludeCharacterMenu(): MenuButton = from(sceneDetails.root).lookup(".add-character").query()
    fun getIncludedCharacter(characterId: String): IncludedCharacterDriver {
        val node: VBox =
            from(sceneDetails.root).lookup(".included-character").queryAll<VBox>().find { it.id == characterId }!!
        return IncludedCharacterDriver(node)
    }

    fun findIncludedCharacter(predicate: (IncludedCharacterDriver) -> Boolean): IncludedCharacterDriver? {
        return from(sceneDetails.root).lookup(".included-character").queryAll<VBox>()
            .asSequence()
            .map(::IncludedCharacterDriver)
            .find(predicate)
    }

    inner class IncludedCharacterDriver(private val node: Node) {
        private fun getMotivationField(): Node = from(node).lookup(".motivation").query()
        fun getMotivationFieldInput(): TextInputControl =
            from(getMotivationField()).lookup(".text-field").queryTextInputControl()

        private fun getPositionOnArcInputField(): Node = from(node).lookup(".position-on-arc").query()
        fun getPositionOnArcInput(): MenuChipGroup =
            from(getPositionOnArcInputField()).lookup(".${menuChipGroup.name}").query()

        fun MenuChipGroup.getArcItem(arcName: String): Menu? = items.find { it.text == arcName } as? Menu
        fun Menu.getArcSectionItem(sectionName: String): MenuItem? = items.find {
            ((it as? CustomMenuItem)?.content as? CheckBox)?.text == sectionName
        }

        fun Menu.getCreateNewSectionOption(): MenuItem = items.find { it.text == "Create Character Arc Section" }!!

        fun Menu.getArcSectionItemOrError(sectionName: String): MenuItem =
            getArcSectionItem(sectionName)
                ?: error("could not find $sectionName in ${items.map { ((it as? CustomMenuItem)?.content as? CheckBox)?.text }}")

        fun MenuItem.isCovered(): Boolean = ((this as? CustomMenuItem)?.content as? CheckBox)?.isSelected == true
    }

}

fun SceneDetails.driver() = SceneDetailsDriver(this)
inline fun SceneDetails.drive(crossinline road: SceneDetailsDriver.() -> Unit) {
    val driver = SceneDetailsDriver(this)
    driver.interact { driver.road() }
}