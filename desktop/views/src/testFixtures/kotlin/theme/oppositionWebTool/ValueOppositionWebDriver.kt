package com.soyle.stories.desktop.view.theme.oppositionWebTool

import com.soyle.stories.common.components.EditableText
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.theme.themeOppositionWebs.Styles
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import org.testfx.api.FxRobot
import tornadofx.uiComponent

class ValueOppositionWebDriver(private val valueOppositionWebTool: ValueOppositionWebs) : FxRobot() {

    fun getCreateValueWebButton(): Button
    {
        return from(valueOppositionWebTool.root).lookup(".create-value-web-button").queryAll<Button>().firstOrNull()
            ?: from(valueOppositionWebTool.root).lookup(".center-button").query()
    }

    private fun getValueWebList(): Parent = from(valueOppositionWebTool.root).lookup(".${Styles.valueWebList.name}").query()

    fun getValueWebItemWithNameOrError(valueWebName: String): Hyperlink =
        getValueWebItemWithName(valueWebName) ?: error("Value Opposition Web Tool does not have value web item with name \"$valueWebName\"")

    fun getValueWebItemWithName(valueWebName: String): Hyperlink? {
        return from(getValueWebList()).lookup(valueWebName).queryAll<Hyperlink>().firstOrNull()
    }

    fun getValueWebNameInput(): EditableText {
        return from(valueOppositionWebTool.root).lookup("#ValueWebName").query<Node>().uiComponent()!!
    }

    private fun getValueWebMenu(): MenuButton = from(valueOppositionWebTool.root).lookup("Actions").query()

    val actions: ValueOppositionWebActions
        get() = ValueOppositionWebActions(getValueWebMenu().items)

    inner class ValueOppositionWebActions internal constructor(private val items: List<MenuItem>) {
        fun delete() = items.find { it.text == "Delete" }!!
    }

    fun getCreateOppositionValueButton(): Button
    {
        return from(valueOppositionWebTool.root).lookup("Add Opposition").query()
    }

    fun getAllOppositionValueCards(): List<Node>
    {
        return from(valueOppositionWebTool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>().toList()
    }

    fun getOppositionValueCardWithName(oppositionValueName: String): Node?
    {
        return getAllOppositionValueCards().find {
            from(it).lookup(oppositionValueName).queryAll<Node>().isNotEmpty()
        }
    }

    fun getOppositionValueNameInput(oppositionValueId: OppositionValue.Id): EditableText
    {
        val card = getAllOppositionValueCards().find { it.id == oppositionValueId.uuid.toString() }
        return from(card).lookup(".opposition-value-name").query<Node>().uiComponent()!!
    }

    fun getOppositionValueNameInput(index: Int): EditableText
    {
        val card = getAllOppositionValueCards()[index]
        return from(card).lookup(".opposition-value-name").query<Node>().uiComponent()!!
    }

    fun getOppositionValueRemoveButton(index: Int): Button
    {
        val card = getAllOppositionValueCards()[index]
        return from(card).lookup(".remove-button").query()
    }

}