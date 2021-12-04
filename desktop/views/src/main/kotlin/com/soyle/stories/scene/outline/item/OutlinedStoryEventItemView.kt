package com.soyle.stories.scene.outline.item

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.scene.outline.SceneOutlineStyles
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.layout.VBox
import tornadofx.*

class OutlinedStoryEventItemView(
    item: OutlinedStoryEventItem,
    actions: OutlinedStoryEventItemActions
) : VBox() {
    init {
        id = item.storyEventId.toString()
        addClass(ComponentsStyles.card)
        addClass(SceneOutlineStyles.outlinedEvent)
    }

    init {
        hbox(alignment = Pos.CENTER_LEFT) {
            addClass(ComponentsStyles.cardHeader)

            label(item.name())
            spacer()
            menubutton {
                contentDisplay = ContentDisplay.GRAPHIC_ONLY
                addClass(ButtonStyles.noArrow)
                addClass(ComponentsStyles.outlined)
                addClass(ComponentsStyles.secondary)
                graphic = MaterialIconView(MaterialIcon.MORE_VERT, "16px").apply {
                    fill = ColorStyles.primaryColor
                }

                item("Remove") {
                    id = "remove"
                    action(actions::remove)
                }
            }
        }
    }
}