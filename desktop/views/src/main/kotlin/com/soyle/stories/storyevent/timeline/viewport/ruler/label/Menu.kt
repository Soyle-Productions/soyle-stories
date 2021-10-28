package com.soyle.stories.storyevent.timeline.viewport.ruler.label

import javafx.scene.control.ContextMenu

interface TimeSpanLabelMenu {
    operator fun invoke(): ContextMenu
}