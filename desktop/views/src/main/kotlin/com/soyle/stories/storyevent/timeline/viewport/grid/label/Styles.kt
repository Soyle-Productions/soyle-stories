package com.soyle.stories.storyevent.timeline.viewport.grid.label

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.storyevent.item.icon.StoryEventItemIconStyles
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import tornadofx.*

class StoryPointLabelStyles : Stylesheet() {
    companion object {

        val storyPointLabel by cssclass()
        val emphasized by csspseudoclass()

        const val STORY_POINT_BORDER_WIDTH = 1
        const val STORY_POINT_H_PADDING = 4
        const val STORY_POINT_V_PADDING = 4
        const val STORY_POINT_GRAPHIC_GAP = 8
        const val STORY_POINT_BORDER_RADIUS = (STORY_POINT_V_PADDING * 2) + (STORY_POINT_BORDER_WIDTH * 2) + StoryEventItemIconStyles.STORY_EVENT_ITEM_ICON_SIZE


        init {
            if (Platform.isFxApplicationThread()) importStylesheet<StoryPointLabelStyles>()
            else runLater { importStylesheet<StoryPointLabelStyles>() }
        }
    }

    init {
        storyPointLabel {
            val radius = STORY_POINT_BORDER_RADIUS
            borderRadius = multi(box(0.px, radius.px, radius.px, 0.px))
            backgroundRadius = multi(box(0.px, radius.px, radius.px, 0.px))
            borderWidth = multi(box(STORY_POINT_BORDER_WIDTH.px))
            padding = box(STORY_POINT_V_PADDING.px, STORY_POINT_H_PADDING.px)
            graphicTextGap = STORY_POINT_GRAPHIC_GAP.px

            and(collapsed) {
                contentDisplay = ContentDisplay.GRAPHIC_ONLY
                minHeight = radius.px
                minWidth = radius.px
            }

            and(hover) {
                backgroundColor += ColorStyles.lightHighlightColor
                textFill = ColorStyles.lightSelectionTextColor
                borderColor = multi(box(ColorStyles.primaryColor))
                borderWidth = multi(box(STORY_POINT_BORDER_WIDTH.px))
                borderInsets = multi(box((-STORY_POINT_BORDER_WIDTH).px))
                and(collapsed) {
                    contentDisplay = ContentDisplay.LEFT
                }
            }

            and(selected) {
                borderColor = multi(box(ColorStyles.primaryColor))
                borderWidth = multi(box(2.px))
                borderInsets = multi(box((-2).px))
                and(hover) {
                    backgroundColor += ColorStyles.lightHighlightColor
                    textFill = ColorStyles.lightSelectionTextColor
                }
            }

            and(emphasized) {
                borderColor = multi(box(ColorStyles.secondaryColor))
                borderWidth = multi(box(4.px))
            }
        }
    }

}