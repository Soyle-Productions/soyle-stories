package com.soyle.stories.common.components.buttons

import com.soyle.stories.common.components.ComponentsStyles
import tornadofx.CssRule

sealed class ButtonVariant {
    abstract val rule: CssRule
    object Filled : ButtonVariant() {
        override val rule: CssRule = ComponentsStyles.filled
    }
    object Outlined : ButtonVariant() {
        override val rule: CssRule = ComponentsStyles.outlined
    }
}