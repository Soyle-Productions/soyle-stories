package com.soyle.stories.desktop.view.theme.characterComparison.addValueButton

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButton
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import tornadofx.hasClass

class `Add Value Button Access`(val button: AddValueButton) : NodeAccess<AddValueButton>(button) {
    companion object :
        NodeAccess.Factory<AddValueButton, AddValueButton, `Add Value Button Access`>(::`Add Value Button Access`)

    val loadingItem: MenuItem?
        get() = button.items.find { it.id == "loading" }

    val createValueWebItem: MenuItem?
        get() = button.items.find { it.id == "create-value-web" }

    val noAvailableValueWebsItem: MenuItem?
        get() = button.items.find { it.id == "no-available-value-webs" }

    val valueWebItems: List<Menu>
        get() = button.items.asSequence()
            .filterIsInstance<Menu>()
            .filter { it.hasClass(AddValueButton.Styles.availableValueWebItem) }
            .toList()

    fun getValueWebItem(valueWebId: ValueWeb.Id): Menu? = valueWebItems.singleOrNull { it.id == valueWebId.toString() }

    val Menu.createOppositionValueItem: MenuItem
        get() = items.single { it.id == "create-opposition-value" }

    val Menu.oppositionValueItems: List<MenuItem>
        get() = items.filter { it.hasClass(AddValueButton.Styles.availableOppositionItem) }

    fun Menu.getOppositionValueItem(oppositionValueId: OppositionValue.Id): MenuItem? =
        oppositionValueItems.singleOrNull { it.id == oppositionValueId.toString() }
}