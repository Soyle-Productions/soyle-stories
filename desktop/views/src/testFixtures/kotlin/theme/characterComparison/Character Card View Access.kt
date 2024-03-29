package com.soyle.stories.desktop.view.theme.characterComparison

import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.characterValueComparison.components.CharacterCard
import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButton
import javafx.scene.Parent
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import tornadofx.CssRule

class `Character Card View Access`(val card: CharacterCard) : NodeAccess<Parent>(card.root) {
    companion object {
        fun CharacterCard.access() = `Character Card View Access`(this)
        fun CharacterCard.access(op: `Character Card View Access`.() -> Unit) = `Character Card View Access`(this).op()
    }

    val addValueButton: AddValueButton by mandatoryChild(AddValueButton.Styles.addValueButton)

    val values: List<Chip>
        get() = from(node).lookup(Chip.Styles.chip.render()).queryAll<Chip>().toList()

    fun getValue(oppositionValueId: OppositionValue.Id): Chip?
    {
        return values.find { it.id == oppositionValueId.uuid.toString() }
    }

}